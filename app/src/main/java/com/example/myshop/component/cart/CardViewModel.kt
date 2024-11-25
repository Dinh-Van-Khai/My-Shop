package com.example.myshop.component.cart

import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel : BaseViewModel<CartState, CartEvent>(CartState()) {

    init {
        getCart()
    }

    private fun getCart() {
        val orders = listOf(
            CartOrder(checked = true, order = Order(
                product = Product(
                    name = "abc xyz",
                    images = listOf("https://i.ytimg.com/vi/tDNGCnQRo24/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLBwIolDEFHJSuTgoUXjtnYqCOhDZQ"),
                    price = 123456
                ),
                variations = mapOf("màu" to "xanh", "cỡ" to "s"),
                quantity = 2
            )),
            CartOrder(checked = true, order = Order(
                product = Product(
                    name = "abc xyz",
                    images = listOf("https://i.ytimg.com/vi/tDNGCnQRo24/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLBwIolDEFHJSuTgoUXjtnYqCOhDZQ"),
                    price = 123456
                ),
                variations = mapOf("màu" to "xanh", "cỡ" to "s"),
                quantity = 2
            ))
        )
        _state.update { it.copy(orders = orders) }
    }

    fun onCartChange(orders: List<CartOrder>) {
        _state.update { it.copy(orders = orders) }
    }

    fun onEditOrderChange(order: Order) {
        _state.update { it.copy(editOrder = order) }
    }

    fun editOrder() {
        viewModelScope.launch {
            onEvent(CartEvent.SetShowBottomSheet(false))
        }
    }

    fun deleteOrderInCart(order: Order) {
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

