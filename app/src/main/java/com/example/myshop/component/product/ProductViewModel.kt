package com.example.myshop.component.product

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.model.Review
import com.example.myshop.model.Stock
import com.example.myshop.model.User
import com.example.myshop.model.Variation
import kotlinx.coroutines.flow.update

class ProductViewModel : BaseViewModel<ProductState, ProductEvent>(ProductState()) {

    val user = User(
        uid = "001",
        profilePicture = "https://scontent.fhan3-5.fna.fbcdn.net/v/t1.6435-1/82209659_1018863901820275_1225272861522919424_n.jpg?stp=cp0_dst-jpg_s40x40&_nc_cat=109&ccb=1-7&_nc_sid=0ecb9b&_nc_eui2=AeFThTHb7R0bmd9GAFvtmQVyQ4-Hp305oWxDj4enfTmhbJCVxxOf1_VDrHnEl9sZvXv03Gqwsb0H2blUQDvvYlXK&_nc_ohc=Rv-DhX1tlc0Q7kNvgEG3Tk6&_nc_zt=24&_nc_ht=scontent.fhan3-5.fna&_nc_gid=AskmXiANNJUeZQ4BbgZu1wh&oh=00_AYDxMRzveoYOYdvsM0XEp58oTWowz9tE5U_1YG-rp3iHew&oe=6759091E",
        name = "AnhKhaiDepTrai",
        address = "Sao Hỏa"
    )
    val shop = User(
        uid = "002",
        profilePicture = "",
        name = "anh khai",
        address = "Sao Hỏa"
    )
    val pro = Product(
        pid = "001",
        name = "sad asdc asc as ca sc asc a sc asc",
        sold = 10000,
        price = 1234567,
        category = listOf("aaa", "bbb"),
        images = listOf(
            "https://scontent.fhan4-4.fna.fbcdn.net/v/t39.30808-6/466671779_1103195374926930_3062086809876660215_n.jpg?stp=dst-jpg_s600x600&_nc_cat=111&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeH9bo6NScoqEdBHQk5sv-IBIfXzoBO-mJAh9fOgE76YkMgff8qDMobUQ12iU7gjKlNQ3y8m46VwFzfGNIOhFAhN&_nc_ohc=6wMtB0d_DqgQ7kNvgGee1Ug&_nc_zt=23&_nc_ht=scontent.fhan4-4.fna&_nc_gid=ARa21qbWCtKtbR-I_wIoVkg&oh=00_AYBcCn4bxlSF6NzqkpdYq8Po6uXerEWTmKzDgEoo996bRA&oe=67383F5E",
            "https://scontent.fhan3-3.fna.fbcdn.net/v/t39.30808-6/466738415_1103195371593597_6088810959779285618_n.jpg?stp=dst-jpg_s600x600&_nc_cat=106&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeFOeuHixY_3IyLWMd1OGiqS4o1ItzcYC33ijUi3NxgLfVYOAdh6vYJpme_RBQQMppCjMuxWg7YDiLh3PaPZJd5g&_nc_ohc=_8IQeKBgoZwQ7kNvgFKcLsj&_nc_zt=23&_nc_ht=scontent.fhan3-3.fna&_nc_gid=ARa21qbWCtKtbR-I_wIoVkg&oh=00_AYDM6HlVM_ExB4obNXn8Iucj1ciB94kfXO7j5iMK653Ptw&oe=67384D4F"
        ),
        user = shop,
        variations = listOf(
            Variation("màu", listOf("xanh", "cam"))
        ),
        description = "asd asd asd asd",
        stocks = listOf(
            Stock(variations = listOf("xanh"), quantity = 3),
            Stock(variations = listOf("cam"), quantity = 5)
        ),
        reviews = listOf(
            Review(user = user, rate = 4, comment = "asdf asdf asdf sdf"),
            Review(user = shop, rate = 5, comment = "asdf asdf asdf sdf")
        )
    )

    init {
        getUser()
        getNumberOfCart()
    }

    private fun getUser() {
        _state.update { it.copy(user = user) }

    }

    fun getInformationProduct(pid: String) {
        _state.update {
            it.copy(
                product = pro,
                order = state.value.order.copy(product = pro)
            )
        }
    }

    private fun getNumberOfCart() {
        _state.update { it.copy(numberOfCart = 5) }

    }

    fun setFirstItemOffset(offset: Offset) {
        _state.update { it.copy(firstItemOffset = offset) }
    }

    fun setSheetContent(sheetContent: SheetContent) {
        _state.update { it.copy(sheetContent = sheetContent) }
    }

    fun onClickCart() {
        Log.e("click", "cart")
        onEvent(ProductEvent.NavigateToCart)

    }

    fun onClickChats() {
        Log.e("click", "chats")
        onEvent(ProductEvent.NavigateToChats)
    }

    fun onClickAddToCart() {
        Log.e("click", "add")
        setSheetContent(SheetContent.ADD_TO_CART)
        onEvent(ProductEvent.SetShowBottomSheet(true))
    }

    fun onClickBuyNow() {
        Log.e("click", "add")
        setSheetContent(SheetContent.BUY_NOW)
        onEvent(ProductEvent.SetShowBottomSheet(true))
    }

    fun onChangeOrder(order: Order) {
        _state.update { it.copy(order = order) }
    }

    fun onAddToCart() {

    }
}

data class ProductState(
    var product: Product = Product(),
    var productOfShop: List<Product> = emptyList(),
    var similarProducts: List<Product> = emptyList(),
    var user: User = User(),
    var order: Order = Order(),
    var sheetContent: SheetContent = SheetContent.ADD_TO_CART,
    var firstItemOffset: Offset = Offset.Zero,
    var numberOfCart: Int = 0,
    var numberOfChats: Int = 0
)

enum class SheetContent {
    DEFAULT, ADD_TO_CART, BUY_NOW
}

sealed class ProductEvent {
    data object ClearFocus: ProductEvent()
    data object BackToPrevScreen: ProductEvent()
    data object NavigateToLogIn: ProductEvent()
    data object NavigateToCart: ProductEvent()
    data object NavigateToChats: ProductEvent()
    data class NavigateToMessage(val otherId: String): ProductEvent()
    data class CheckOut(val orders: List<Order>): ProductEvent()
    data class ViewShop(val uid: String): ProductEvent()
    data class ViewProduct(val product: Product): ProductEvent()
    data class ShowSnackBar(val message: String): ProductEvent()
    data class SetShowBottomSheet(val status: Boolean): ProductEvent()
}