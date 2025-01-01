package com.example.myshop.component.my_shop_purchase

import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.FCMToken
import com.example.myshop.model.Notification
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.model.PushNotification
import com.example.myshop.util.NotificationRepository
import com.example.myshop.util.OrderStatus.CANCELLED
import com.example.myshop.util.OrderStatus.ORDERED
import com.example.myshop.util.OrderStatus.SHIPPED
import com.example.myshop.util.OrderStatus.SHIPPING
import com.example.myshop.util.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
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
class MyShopPurchaseViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val notificationRepository: NotificationRepository
) : BaseViewModel<MyShopPurchaseState, MyShopPurchaseEvent>(MyShopPurchaseState()) {

    private val productsCollectionRef = Firebase.firestore.collection("products")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")
    private val tokensCollectionRef = Firebase.firestore.collection("tokens")
    private val notificationsCollectionRef = Firebase.firestore.collection("notifications")

    private var myShopPurchaseListener: ListenerRegistration? = null

    init {
        getMyShopPurchase()
    }

    private fun getMyShopPurchase() {
        auth.currentUser?.run {
            myShopPurchaseListener = ordersCollectionRef
                .whereNotEqualTo("orderedDate", null)
                .orderBy("orderedDate", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapShot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    querySnapShot?.let { snapShot ->
                        val orders = snapShot.documents
                            .mapNotNull { it.toObject<Order>() }
                            .filter { it.product.user.uid == uid }
                        _state.update { state ->
                            state.copy(
                                allOrders = orders,
                                orderedOrders = orders.filter { it.status == ORDERED },
                                shippingOrders = orders.filter { it.status == SHIPPING },
                                shippedOrders = orders.filter { it.status == SHIPPED },
                                cancelledOrders = orders.filter { it.status == CANCELLED }
                            )
                        }
                    }
                }
        }
    }

    fun setNewTab(newTab: Int) {
        _state.update { it.copy(oldTab = it.currentTab, currentTab = newTab) }
    }

    fun confirmOrder(order: Order) {
        viewModelScope.launch {
            try {

                val orderDocumentRef = ordersCollectionRef.document(order.oid)
                orderDocumentRef.update("status", SHIPPING).await()

                sendNotification(
                    order = order,
                    title = "Confirm Order ${order.product.name}",
                    message = "${order.product.user.name} has confirmed your order"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            try {

                val orderDocumentRef = ordersCollectionRef.document(order.oid)
                orderDocumentRef.update(
                    mapOf(
                        Pair("status", CANCELLED),
                        Pair("cancelledDate", Date(System.currentTimeMillis()))
                    )
                ).await()

                sendNotification(
                    order = order,
                    title = "Cancel Order ${order.product.name}",
                    message = "${order.product.user.name} has cancelled your order"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }


    fun confirmShipped(order: Order) {
        viewModelScope.launch {
            try {
                val isSuccessful = Firebase.firestore.runTransaction<Boolean> { transaction ->
                    val productDocumentRef = productsCollectionRef.document(order.product.pid)
                    val orderDocumentRef = ordersCollectionRef.document(order.oid)

                    val snapShot = transaction.get(productDocumentRef)
                    snapShot.toObject<Product>()?.run {
                        stocks.find {
                            it.variations.containsAll(order.variations.values)
                        }?.let { stock ->
                            val index = stocks.indexOf(stock)
                            val tempStocks = stocks.toMutableList()
                            tempStocks[index] = tempStocks[index].copy(
                                quantity = tempStocks[index].quantity - order.quantity
                            )

                            transaction.update(
                                orderDocumentRef,
                                mapOf(
                                    Pair("status", SHIPPED),
                                    Pair("shippedDate", Date(System.currentTimeMillis()))
                                )
                            )
                            transaction.update(
                                productDocumentRef,
                                mapOf(
                                    Pair("sold", sold + order.quantity),
                                    Pair("stocks", tempStocks)
                                )
                            )
                            return@runTransaction true
                        }
                    }
                    return@runTransaction false
                }.await()

                if (isSuccessful) {
                    sendNotification(
                        order = order,
                        title = "Confirm Shipped Order ${order.product.name}",
                        message = "${order.product.user.name} has shipped your order, you can review the product"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    private suspend fun sendNotification(order: Order, title: String, message: String) {
        val tokenDocumentRef = tokensCollectionRef.document(order.customer.uid)
        val tokenDocumentSnapShot = tokenDocumentRef.get().await()

        val notificationDocumentRef = notificationsCollectionRef.document()
        val notification = Notification(
            nid = notificationDocumentRef.id,
            to = order.customer.uid,
            image = order.product.images[0],
            title = title,
            message = message,
            date = Date(System.currentTimeMillis()),
            seen = false,
            read = false,
            route = Screen.MyPurchase.route
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

    override fun onCleared() {
        super.onCleared()
        myShopPurchaseListener?.remove()
    }
}

data class MyShopPurchaseState(
    var oldTab: Int = 0,
    var currentTab: Int = 0,
    var allOrders: List<Order> = emptyList(),
    var orderedOrders: List<Order> = emptyList(),
    var shippingOrders: List<Order> = emptyList(),
    var shippedOrders: List<Order> = emptyList(),
    var cancelledOrders: List<Order> = emptyList(),
)

sealed class MyShopPurchaseEvent {
    object BackToMyShopScreen: MyShopPurchaseEvent()
}