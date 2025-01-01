package com.example.myshop.component.cart

import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.util.OrderStatus.ADDED_TO_CART
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
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
class CartViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<CartState, CartEvent>(CartState()) {

    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private var cartListener: ListenerRegistration? = null

    init {
        getCart()
    }

    private fun getCart() {
        auth.currentUser?.run {
            cartListener = ordersCollectionRef.where(
                Filter.and(
                    Filter.equalTo("customer.uid", uid),
                    Filter.equalTo("status", ADDED_TO_CART)
                )
            ).addSnapshotListener { querySnapShot, error ->
                error?.let { e ->
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                querySnapShot?.let { snapShot ->
                    val orders = snapShot.documents
                        .mapNotNull { it.toObject<Order>() }
                        .map { CartOrder(order = it, checked = false) }
                    _state.update { it.copy(orders = orders) }
                }
            }
        }
    }

    fun onCartChange(orders: List<CartOrder>) {
        _state.update { it.copy(orders = orders) }
    }

    fun onEditOrderChange(order: Order) {
        _state.update { it.copy(editOrder = order) }
    }

    fun editOrder() {
        viewModelScope.launch {
            try {
                val documentRef = ordersCollectionRef.document(state.value.editOrder.oid)
                documentRef.set(state.value.editOrder).await()
                onEvent(CartEvent.SetShowBottomSheet(false))
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    fun deleteOrderInCart(order: Order) {
        viewModelScope.launch {
            try {
                val documentRef = ordersCollectionRef.document(order.oid)
                documentRef.delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cartListener?.remove()
    }

}

data class CartState(
    var orders: List<CartOrder> = emptyList(),
    var editOrder: Order = Order()
) {
    fun getTotalCost(): Long {
        val result = orders.filter {
            it.checked
        }.sumOf {
            it.order.product.price * it.order.quantity
        }
        return result
    }
}

data class CartOrder(
    var order: Order = Order(),
    var checked: Boolean = false
)

sealed class CartEvent {
    object BackToPrevScreen: CartEvent()
    data class SetShowBottomSheet(val status: Boolean): CartEvent()
    data class NavigateToProduct(val product: Product): CartEvent()
    data class CheckOut(val orders: List<Order>): CartEvent()
}

