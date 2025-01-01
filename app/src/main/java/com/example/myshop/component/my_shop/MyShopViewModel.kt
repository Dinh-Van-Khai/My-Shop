package com.example.myshop.component.my_shop

import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Product
import com.example.myshop.model.User
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
class MyShopViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<MyShopState, MyShopEvent>(MyShopState()) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val productsCollectionRef = Firebase.firestore.collection("products")

    private var userListener: ListenerRegistration? = null
    private var productsListener: ListenerRegistration? = null

    init {
        getShopInformation()
    }

    private fun getShopInformation() {
        viewModelScope.launch {
            auth.currentUser?.run {

                userListener = usersCollectionRef
                    .document(uid)
                    .addSnapshotListener { documentSnapshot, error ->
                        error?.let { e ->
                            e.printStackTrace()
                            return@addSnapshotListener
                        }
                        documentSnapshot?.let { snapShot ->
                            snapShot.toObject<User>()?.let { user ->
                                _state.update { it.copy(user = user) }
                            }
                        }
                    }

                productsListener = productsCollectionRef
                    .whereEqualTo("user.uid", uid)
                    .addSnapshotListener { querySnapshot, error ->
                        error?.let {
                            return@addSnapshotListener
                        }
                        querySnapshot?.let { snapshot ->
                            val products = snapshot.documents.mapNotNull { it.toObject<Product>() }
                            _state.update { it.copy(products = products) }
                        }
                    }
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                val productDocumentRef = productsCollectionRef.document(product.pid)
                productDocumentRef.delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        productsListener?.remove()
    }
}

data class MyShopState(
    var user: User = User(),
    var products: List<Product> = emptyList()
)

sealed class MyShopEvent {
    object BackToProfile : MyShopEvent()
    object AddProduct : MyShopEvent()
    object NavigateToMyShopPurchases : MyShopEvent()
    data class EditProduct(val product: Product): MyShopEvent()
    data class ShowSnackBar(val message: String): MyShopEvent()
}
