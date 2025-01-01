package com.example.myshop.component.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Category
import com.example.myshop.model.Product
import com.example.myshop.util.OrderStatus.ADDED_TO_CART
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<HomeState, HomeEvent>(HomeState()) {

    private val eventCollectionRef = Firebase.firestore.collection("event")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")
    private val productsCollectionRef = Firebase.firestore.collection("products")

    private var numberOfCartListener: ListenerRegistration? = null

    init {
        //For all (event not logged in)
        getEventImages()
        getCategories()
        getAllProduct()
        getSearchResult()

        //For users logged in
        getNumberOfCart()
    }

    private fun getEventImages() {
        val imagesDocumentRef = eventCollectionRef.document("images")
        imagesDocumentRef.addSnapshotListener { documentSnapShot, error ->
            error?.let { exception ->
                exception.printStackTrace()
                return@addSnapshotListener
            }
            documentSnapShot?.let { snapShot ->
                val eventImages = snapShot.toObject<EventImages>()
                eventImages?.let { images ->
                    _state.update { it.copy(eventImages = images.images) }
                }
            }
        }
    }

    private fun getCategories() {
        val categoriesCollectionRef = Firebase.firestore.collection("categories")
        categoriesCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let { exception ->
                exception.printStackTrace()
                return@addSnapshotListener
            }
            querySnapshot?.let { snapShot ->
                val categories = mutableListOf("All")
                categories += snapShot.documents
                    .mapNotNull { it.toObject<Category>() }
                    .map { it.name }
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    private fun getAllProduct() {
        productsCollectionRef.addSnapshotListener { querySnapshot, error ->
            error?.let { exception ->
                exception.printStackTrace()
                return@addSnapshotListener
            }
            querySnapshot?.let { snapshot ->
                val allProduct = snapshot.documents.mapNotNull { it.toObject<Product>() }
                _state.update {
                    it.copy(
                        allProduct = allProduct,
                        showingProduct = allProduct.filter { product ->
                            state.value.currentCategory == "All" || state.value.currentCategory == product.category[0]
                        }
                    )
                }
            }
        }
    }

    private fun getSearchResult() {
        state.map { it.searchText }
            .distinctUntilChanged()
            .debounce(300L)
            .filter { it.isNotEmpty() }
            .onEach { searchText ->
                _state.update {
                    it.copy(
                        searchResult = it.allProduct.filter { product ->
                            product.toString().contains(searchText, ignoreCase = true)
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
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

    fun onSearchTextChange(newValue: String) {
        _state.update { it.copy(searchText = newValue) }
    }

    fun onClickCart() {
        if (auth.currentUser != null) {
            onEvent(HomeEvent.NavigateToCart)
        } else {
            onEvent(HomeEvent.NavigateToLogIn)
        }
    }

    fun onClickChats() {
        if (auth.currentUser != null) {
            onEvent(HomeEvent.NavigateToChats)
        } else {
            onEvent(HomeEvent.NavigateToLogIn)
        }
    }

    fun setCurrentCategory(value: String) {
        _state.update {
            it.copy(
                currentCategory = value,
                showingProduct = state.value.allProduct.filter { product ->
                    value == "All" || value == product.category[0]
                }
            )
        }
    }
}

data class HomeState(
    var eventImages: List<String> = emptyList(),
    var categories: List<String> = listOf("All"),
    var currentCategory: String = "All",
    var allProduct: List<Product> = emptyList(),
    var showingProduct: List<Product> = emptyList(),
    var searchText: String = "",
    var searchResult: List<Product> = emptyList(),
    var numberOfCart: Int = 0,
    var numberOfChats: Int = 0,

    )

data class EventImages(
    var images: List<String> = emptyList()
)
sealed class HomeEvent {
    data object NavigateToLogIn : HomeEvent()
    data object NavigateToCart : HomeEvent()
    data object NavigateToChats : HomeEvent()
    data class NavigateToProduct(val product: Product) : HomeEvent()
}