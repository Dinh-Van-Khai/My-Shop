package com.example.myshop.component.message

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.FCMToken
import com.example.myshop.model.Message
import com.example.myshop.model.Notification
import com.example.myshop.model.PushNotification
import com.example.myshop.model.User
import com.example.myshop.util.NotificationRepository
import com.example.myshop.util.Screen
import com.example.myshop.util.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@Suppress("DEPRECATION")
@HiltViewModel
class MessageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val notificationRepository: NotificationRepository,
    private val appContext: Application
) : BaseViewModel<MessageState, MessageEvent>(MessageState()) {

    private val usersCollection = Firebase.firestore.collection("users")
    private val messagesCollection = Firebase.firestore.collection("messages")
    private val tokensCollection = Firebase.firestore.collection("tokens")

    private val messagesStorageRef = Firebase.storage.reference.child("messages")

    init {
        getUser()
    }

    private fun getUser() {
        auth.currentUser?.run {
            usersCollection.document(uid).addSnapshotListener { documentSnapShot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                documentSnapShot?.let { snapShot ->
                    snapShot.toObject<User>()?.let { user ->
                        _state.update { it.copy(user = user) }
                    }
                }
            }
        }
    }

    fun getOthers(othersId: String) {
        usersCollection.document(othersId).addSnapshotListener { documentSnapShot, error ->
            error?.let { e ->
                e.printStackTrace()
                return@addSnapshotListener
            }
            documentSnapShot?.let { snapShot ->
                snapShot.toObject<User>()?.let { user ->
                    _state.update { it.copy(others = user) }
                }
            }
        }
    }

    fun getMessages(othersId: String) {
        auth.currentUser?.run {
            messagesCollection
                .where(
                    Filter.or(
                        Filter.and(
                            Filter.equalTo("from.uid", uid),
                            Filter.equalTo("to.uid", othersId)
                        ),
                        Filter.and(
                            Filter.equalTo("from.uid", othersId),
                            Filter.equalTo("to.uid", uid)
                        )
                    )
                )
                .addSnapshotListener { querySnapShot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    querySnapShot?.let { snapShot ->
                        val messages = snapShot.documents
                            .mapNotNull { it.toObject<Message>() }
                            .sortedByDescending { it.date }
                        _state.update { it.copy(messages = messages) }

                        //Mark message to user as read
                        messages.filter {
                            it.to.uid == uid && !it.read
                        }.onEach {
                            maskMessageAsRead(it.mid)
                        }
                    }
                }
        }
    }

    private fun maskMessageAsRead(mid: String) {
        viewModelScope.launch {
            try {
                messagesCollection.document(mid).update("read", true).await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }


    fun onTextChange(newValue: String) {
        _state.update { it.copy(text = newValue) }
    }

    fun onImagesChange(images: List<Uri>) {
        _state.update { it.copy(images = images) }
    }

    fun onSendMessage() {
        viewModelScope.launch {
            val tempState = state.value
            _state.update {
                it.copy(
                    text = "",
                    images = emptyList(),
                    sending = true
                )
            }
            onEvent(MessageEvent.ScrollToFirstItem)
            try {
                val messageDocument = messagesCollection.document()

                val groupId = if (tempState.user.uid < tempState.others.uid) {
                    "${tempState.user.uid}_${tempState.others.uid}"
                } else {
                    "${tempState.others.uid}_${tempState.user.uid}"
                }

                val messageRef = messagesStorageRef.child(groupId)
                var images = emptyList<String>()

                tempState.images.forEach { uri ->
                    val fileExt = getFileExtension(uri, appContext.contentResolver)
                    val fileName = "${UUID.randomUUID()}-${System.currentTimeMillis()}.$fileExt"

                    val messageImageRef = messageRef.child(fileName)

                    val taskSnapShot = messageImageRef.putFile(uri).await()
                    val task = taskSnapShot.task
                    if (task.isComplete) {
                        val url = messageImageRef.downloadUrl.await()
                        val path = url?.toString() ?: ""
                        images = images + path
                    }
                }

                val message = Message(
                    mid = messageDocument.id,
                    from = tempState.user,
                    to = tempState.others,
                    groupId = groupId,
                    text = tempState.text,
                    images = images,
                    date = Date(System.currentTimeMillis()),
                    read = false
                )
                _state.update { it.copy(sending = false) }
                messageDocument.set(message).await()
                sendNotification(message)
            } catch (e: Exception) {
                _state.update { it.copy(sending = false) }
                e.printStackTrace()
                return@launch
            }
        }
    }

    private suspend fun sendNotification(message: Message) {
        val tokenDocumentRef = tokensCollection.document(message.to.uid)
        val tokenDocumentSnapShot = tokenDocumentRef.get().await()

        tokenDocumentSnapShot.toObject<FCMToken>()?.run {
            val postNotification = PushNotification(
                data = Notification(
                    title = message.from.name,
                    message = message.text.ifBlank { "Sent ${message.images.size} images" },
                    route = Screen.Chats.route
                ),
                to = token
            )
            notificationRepository.postNotification(postNotification)
        }
    }
}

data class MessageState(
    var user: User = User(),
    var others: User = User(),
    var messages: List<Message> = emptyList(),

    var text: String = "",
    var images: List<Uri> = emptyList(),

    var sending: Boolean = false
)

sealed class MessageEvent {
    object ClearFocus : MessageEvent()
    object BackToPrevScreen : MessageEvent()
    object ScrollToFirstItem : MessageEvent()
}