package com.example.myshop.component.product

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.model.User
import com.example.myshop.util.OrderStatus.ADDED_TO_CART
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Suppress("DEPRECATION")
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<ProductState, ProductEvent>(ProductState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val productsCollectionRef = Firebase.firestore.collection("products")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private var numberOfCartListener: ListenerRegistration? = null

    init {
        getUser()
        getNumberOfCart()
    }

    private fun getUser() {
        viewModelScope.launch {
            auth.currentUser?.run {
                usersCollectionRef
                    .document(uid)
                    .addSnapshotListener { documentSnapShot, error ->
                        error?.let { e ->
                            e.printStackTrace()
                            return@addSnapshotListener
                        }
                        documentSnapShot?.let { snapShot ->
                            val getUser = snapShot.toObject<User>()
                            getUser?.let { user ->
                                _state.update { it.copy(user = user) }
                            }
                        }
                    }
            }
        }
    }

    fun getInformationProduct(pid: String) {

        // Observe product
        val productDocument = productsCollectionRef.document(pid)
        productDocument.addSnapshotListener { documentSnapshot, error ->
            error?.let { e ->
                e.printStackTrace()
                return@addSnapshotListener
            }
            documentSnapshot?.let { snapshot ->
                snapshot.toObject<Product>()?.let { product ->
                    _state.update {
                        it.copy(
                            product = product,
                            order = state.value.order.copy(product = product)
                        )
                    }

                    //Observe product of shop
                    productsCollectionRef
                        .whereEqualTo("user.uid", product.user.uid)
                        .addSnapshotListener { querySnapshot, error ->
                            error?.let { e ->
                                e.printStackTrace()
                                return@addSnapshotListener
                            }
                            querySnapshot?.let { snapshot ->
                                val productsOfShop = snapshot.documents
                                    .mapNotNull { it.toObject<Product>() }
                                    .filter { it.pid != state.value.product.pid }
                                _state.update { it.copy(productOfShop = productsOfShop) }
                            }
                        }

                    //Observe similar product
                    productsCollectionRef
                        .whereArrayContainsAny("category", product.category)
                        .orderBy("sold", Query.Direction.DESCENDING)
                        .addSnapshotListener { querySnapshot, error ->
                            error?.let { e ->
                                e.printStackTrace()
                                return@addSnapshotListener
                            }
                            querySnapshot?.let { snapshot ->
                                val similarProduct = snapshot.documents
                                    .mapNotNull { it.toObject<Product>() }
                                    .filter { it.pid != state.value.product.pid }
                                _state.update { it.copy(similarProducts = similarProduct) }
                            }
                        }

                }
            }
        }

    }

    private fun getNumberOfCart() {
        auth.addAuthStateListener {
            numberOfCartListener?.remove()

            if (auth.currentUser == null) {
                _state.update { it.copy(numberOfCart = 0) }
            }

            auth.currentUser?.run {
                numberOfCartListener = ordersCollectionRef
                    .where(
                        Filter.and(
                            Filter.equalTo("customer.uid", uid),
                            Filter.equalTo("status", ADDED_TO_CART)
                        )
                    )
                    .addSnapshotListener { querySnapShot, error ->
                        error?.let { e ->
                            e.printStackTrace()
                            return@addSnapshotListener
                        }
                        querySnapShot?.let { snapShot ->
                            _state.update { it.copy(numberOfCart = snapShot.documents.size) }
                        }
                    }
            }
        }
    }

    fun setFirstItemOffset(offset: Offset) {
        _state.update { it.copy(firstItemOffset = offset) }
    }

    fun setSheetContent(sheetContent: SheetContent) {
        _state.update { it.copy(sheetContent = sheetContent) }
    }

    fun onClickCart() {
        if (auth.currentUser != null) {
            onEvent(ProductEvent.NavigateToCart)
        } else {
            onEvent(ProductEvent.NavigateToLogIn)
        }
    }

    fun onClickChats() {
        if (auth.currentUser != null) {
            onEvent(ProductEvent.NavigateToChats)
        } else {
            onEvent(ProductEvent.NavigateToLogIn)
        }
    }

    fun onClickAddToCart() {
        if (auth.currentUser != null) {
            setSheetContent(SheetContent.ADD_TO_CART)
            onEvent(ProductEvent.SetShowBottomSheet(true))
        } else {
            onEvent(ProductEvent.NavigateToLogIn)
        }
    }

    fun onClickBuyNow() {
        if (auth.currentUser != null) {
            setSheetContent(SheetContent.BUY_NOW)
            onEvent(ProductEvent.SetShowBottomSheet(true))
        } else {
            onEvent(ProductEvent.NavigateToLogIn)
        }
    }

    fun onChangeOrder(order: Order) {
        _state.update { it.copy(order = order) }
    }

    fun onAddToCart() {
        viewModelScope.launch {
            try {
                val orderDocumentRef = ordersCollectionRef.document()
                val order = state.value.order.copy(
                    oid = orderDocumentRef.id,
                    customer = state.value.user,
                    product = state.value.product,
                    status = ADDED_TO_CART
                )
                orderDocumentRef.set(order).await()
                onEvent(ProductEvent.SetShowBottomSheet(false))
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
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