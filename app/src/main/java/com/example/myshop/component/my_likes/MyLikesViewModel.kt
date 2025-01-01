package com.example.myshop.component.my_likes

import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Like
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MyLikesViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<MyLikesState, MyLikesEvent>(MyLikesState()) {

    private val likesCollectionRef = Firebase.firestore.collection("likes")
    private var likedShopsListener: ListenerRegistration? = null

    init {
        getLikedShops()
    }

    private fun getLikedShops() {
        _state.update { it.copy(likes = emptyList()) }
        auth.currentUser?.run {
            likedShopsListener = likesCollectionRef
                .whereEqualTo("liker.uid", uid)
                .addSnapshotListener { querySnapshot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    querySnapshot?.let { snapshot ->
                        val likes = snapshot.documents.mapNotNull { it.toObject<Like>() }
                        _state.update { it.copy(likes = likes) }
                    }
                }
        }
    }

    fun unlikeShop(like: Like) {
        viewModelScope.launch {
            try {
                val likeRef = likesCollectionRef.document(like.lid)
                likeRef.delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        likedShopsListener?.remove()
    }
}

data class MyLikesState(
    var likes: List<Like> = emptyList(),
)

sealed class MyLikesEvent {
    object BackToPrevScreen: MyLikesEvent()
    data class ViewShop(val uid: String) : MyLikesEvent()
}
