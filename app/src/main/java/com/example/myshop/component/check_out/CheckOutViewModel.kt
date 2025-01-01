package com.example.myshop.component.check_out

import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.FCMToken
import com.example.myshop.model.Notification
import com.example.myshop.model.Order
import com.example.myshop.model.PushNotification
import com.example.myshop.model.User
import com.example.myshop.util.NotificationRepository
import com.example.myshop.util.OrderStatus.ORDERED
import com.example.myshop.util.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CheckOutViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val notificationRepository: NotificationRepository
) : BaseViewModel<CheckOutState, CheckOutEvent>(CheckOutState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")
    private val tokensCollectionRef = Firebase.firestore.collection("tokens")
    private val notificationsCollectionRef = Firebase.firestore.collection("notifications")

    init {
        getUserInformation()
    }

    private fun getUserInformation() {
        viewModelScope.launch {
            auth.currentUser?.run {
                val userDocumentRef = usersCollectionRef.document(uid)
                val documentSnapShot = userDocumentRef.get().await()
                documentSnapShot?.let { snapShot ->
                    snapShot.toObject<User>()?.let { user ->
                        _state.update { it.copy(user = user) }
                    }
                }
            }
        }
    }

    fun onOrdersChange(orders: List<Order>) {
        _state.update { it.copy(orders = orders) }
    }

    fun placeOrder() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(loading = true) }
                state.value.orders.forEach { order ->
                    val orderDocumentRef = if (order.oid.isNotBlank()) {
                        ordersCollectionRef.document(order.oid)
                    } else {
                        ordersCollectionRef.document()
                    }
                    orderDocumentRef.set(
                        order.copy(
                            oid = orderDocumentRef.id,
                            customer = state.value.user,
                            status = ORDERED,
                            orderedDate = Date(System.currentTimeMillis()),
                            shippedDate = null,
                            cancelledDate = null
                        )
                    ).await()

                    val tokenDocumentRef = tokensCollectionRef.document(order.product.user.uid)
                    val tokenDocumentSnapShot = tokenDocumentRef.get().await()

                    val notificationDocumentRef = notificationsCollectionRef.document()
                    val notification = Notification(
                        nid = notificationDocumentRef.id,
                        to = order.product.user.uid,
                        image = order.product.images[0],
                        title = "Order product ${order.product.name}",
                        message = "${state.value.user.name} just placed an order for you, please confirm",
                        date = Date(System.currentTimeMillis()),
                        seen = false,
                        read = false,
                        route = Screen.MyShopPurchases.route
                    )
                    notificationDocumentRef.set(notification).await()

                    tokenDocumentSnapShot.toObject<FCMToken>()?.run {
                        val postNotification = PushNotification(
                            data = Notification(
                                title = notification.title,
                                message = notification.message,
                                route = Screen.Notification.route
                            ),
                            to = token
                        )
                        notificationRepository.postNotification(postNotification)
                    }
                }
                _state.update { it.copy(loading = false) }
                onEvent(CheckOutEvent.BackToPrevScreen)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(loading = false) }
                onEvent(CheckOutEvent.ShowSnackBar("Error! Can not place order"))
            }
        }
    }
}

data class CheckOutState(
    var loading: Boolean = false,
    var user: User = User(),
    var orders: List<Order> = emptyList()
)

sealed class CheckOutEvent {
    object ClearFocus : CheckOutEvent()
    object BackToPrevScreen : CheckOutEvent()
    data class ShowSnackBar(val message: String) : CheckOutEvent()
}