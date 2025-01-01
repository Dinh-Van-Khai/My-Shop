package com.example.myshop.component.my_purchase

import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.util.OrderStatus.CANCELLED
import com.example.myshop.util.OrderStatus.ORDERED
import com.example.myshop.util.OrderStatus.SHIPPED
import com.example.myshop.util.OrderStatus.SHIPPING
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
class MyPurchaseViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<MyPurchaseState, MyPurchaseEvent>(MyPurchaseState()) {

    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private var purchaseListener: ListenerRegistration? = null

    init {
        getPurchase()
    }

    private fun getPurchase() {
        auth.currentUser?.run {
            purchaseListener = ordersCollectionRef
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
                            .filter { it.customer.uid == uid }
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

    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            try {
                val orderDocumentRef = ordersCollectionRef.document(order.oid)
                orderDocumentRef.set(
                    order.copy(
                        status = CANCELLED,
                        cancelledDate = Date(System.currentTimeMillis())
                    )
                ).await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        purchaseListener?.remove()
    }

}

data class MyPurchaseState(
    var oldTab: Int = 0,
    var currentTab: Int = 0,
    var allOrders: List<Order> = emptyList(),
    var orderedOrders: List<Order> = emptyList(),
    var shippingOrders: List<Order> = emptyList(),
    var shippedOrders: List<Order> = emptyList(),
    var cancelledOrders: List<Order> = emptyList(),
)

sealed class MyPurchaseEvent {
    object BackToPrevScreen: MyPurchaseEvent()
    data class ViewProduct(val product: Product): MyPurchaseEvent()
    data class CheckOut(val orders: List<Order>): MyPurchaseEvent()
    data class Review(val order: Order): MyPurchaseEvent()
}