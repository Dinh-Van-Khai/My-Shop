package com.example.myshop.component.edit_product

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Category
import com.example.myshop.model.Product
import com.example.myshop.model.Stock
import com.example.myshop.model.User
import com.example.myshop.model.Variation
import com.example.myshop.util.ProductConstant
import com.example.myshop.util.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val appContext: Application
) : BaseViewModel<EditProductState, EditProductEvent>(EditProductState()) {

    private val categoriesCollectionRef = Firebase.firestore.collection("categories")
    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val productsCollectionRef = Firebase.firestore.collection("products")

    private val productsStorageRef = Firebase.storage.reference.child("products")

    init {
        getUser()
        observeCategories()
    }

    private fun getUser() {
        auth.currentUser?.run {
            viewModelScope.launch {
                val userDocumentRef = usersCollectionRef.document(uid)
                userDocumentRef.addSnapshotListener { documentSnapShot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    documentSnapShot?.let { snapShot ->
                        snapShot.toObject<User>()?.let { user ->
                            _state.update { it.copy(product = it.product.copy(user = user)) }
                        }
                    }
                }
            }
        }
    }

    private fun observeCategories() {
        categoriesCollectionRef.addSnapshotListener { snapShot, error ->
            error?.let { e ->
                e.printStackTrace()
                return@addSnapshotListener
            }
            snapShot?.let { querySnapShot ->
                val categories = querySnapShot.documents.mapNotNull { it.toObject<Category>() }
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    fun setInitProduct(product: Product) {
        _state.update { it.copy(product = product) }
    }

    fun setSheetContent(sheetContent: SheetContent) {
        _state.update { it.copy(sheetContent = sheetContent) }
    }

    fun onChangeExistImages(images: List<String>) {
        _state.update { it.copy(product = it.product.copy(images = images)) }
    }

    fun onChangeImages(images: List<Uri>) {
        _state.update {
            it.copy(
                images = if (images.size <= ProductConstant.MAX_IMAGES) images else images.subList(
                    0,
                    ProductConstant.MAX_IMAGES
                )
            )
        }
    }

    fun onChangeName(value: String) {
        _state.update { it.copy(product = it.product.copy(name = value)) }
    }

    fun onChangeDescription(value: String) {
        _state.update { it.copy(product = it.product.copy(description = value)) }
    }

    fun onChangeCategory(category: List<String>) {
        _state.update {
            it.copy(
                product = it.product.copy(category = category),
                currentCategory = state.value.categories.find { category1 ->
                    category1.name == category[0]
                } ?: Category()
            )
        }
    }

    fun onChangePrice(value: Long) {
        _state.update { it.copy(product = it.product.copy(price = value)) }
    }

    fun onChangeVariations(value: List<Variation>) {
        _state.update { it.copy(product = it.product.copy(variations = value)) }

        if (state.value.product.variations.size == 1) {
            _state.update { it.copy(product = it.product.copy(stocks = emptyList())) }
            state.value.product.variations[0].child.forEach { name ->
                _state.update {
                    it.copy(product = it.product.copy(stocks = it.product.stocks + Stock(listOf(name))))
                }
            }
        } else if (state.value.product.variations.size == 2) {
            _state.update { it.copy(product = it.product.copy(stocks = emptyList())) }
            state.value.product.variations[0].child.forEach { name1 ->
                state.value.product.variations[1].child.forEach { name2 ->
                    _state.update {
                        it.copy(
                            product = it.product.copy(
                                stocks = it.product.stocks + Stock(
                                    listOf(
                                        name1, name2
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun setStocks(value: List<Stock>) {
        _state.update { it.copy(product = it.product.copy(stocks = value)) }
    }

    fun onAdd() {
        if (state.value.product.images.isEmpty() && state.value.images.isEmpty()) {
            onEvent(EditProductEvent.ShowSnackBar("The Product must have at least one image"))
            return
        }

        try {
            checkProduct(state.value.product)
        } catch (e: CheckProductException) {
            onEvent(EditProductEvent.ShowSnackBar(e.message ?: "Invalid input product"))
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                val documentRef = if (state.value.product.pid.isNotBlank()) {
                    productsCollectionRef.document(state.value.product.pid)
                } else {
                    productsCollectionRef.document()
                }
                _state.update { it.copy(product = it.product.copy(pid = documentRef.id)) }

                val productRef = productsStorageRef.child("${documentRef.id}/images-product")

                state.value.images.forEach { uri ->
                    val fileExt = getFileExtension(uri, appContext.contentResolver)
                    val fileName = "${UUID.randomUUID()}-${System.currentTimeMillis()}.$fileExt"

                    val productImageRef = productRef.child(fileName)

                    val taskSnapShot = productImageRef.putFile(uri).await()
                    val task = taskSnapShot.task
                    if (task.isComplete) {
                        val url = productImageRef.downloadUrl.await()
                        val path = url?.toString() ?: ""

                        val tempImages = state.value.images.toMutableList()
                        tempImages.remove(uri)

                        _state.update {
                            it.copy(
                                product = it.product.copy(images = it.product.images + path),
                                images = tempImages
                            )
                        }
                    }
                }
                documentRef.set(state.value.product).await()
                _state.update { it.copy(isLoading = false) }
                onEvent(EditProductEvent.BackToShop)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                onEvent(EditProductEvent.ShowSnackBar(e.message ?: "Can't add product"))
                e.printStackTrace()
                return@launch
            }
        }
    }

    private fun checkProduct(product: Product) {
        if (product.name.length < ProductConstant.MIN_NAME) {
            throw CheckProductException("The Product name must have at least 10 character")
        }

        if (product.description.length < ProductConstant.MIN_DESCRIPTION) {
            throw CheckProductException("The Product description must have at least 30 character")
        }

        if (product.category.size != 2) {
            throw CheckProductException("You have to complete Product Category")
        }

        if (
            product.variations.isEmpty() || product.variations.any {
                it.name.isBlank() || it.child.isEmpty() || it.child.any { name -> name.isBlank() }
            }
        ) {
            throw CheckProductException("You have to complete Product variations")
        }

        if (product.stocks.all { it.quantity == 0L }) {
            throw CheckProductException("You have to complete Product stock")
        }

        if (product.price < ProductConstant.MIN_PRICE) {
            throw CheckProductException("Product price must be from 1000đ or more")
        }
    }

    private class CheckProductException(message: String) : Exception(message)
}

data class EditProductState(
    var images: List<Uri> = emptyList(),

    var product: Product = Product(),
    var isLoading: Boolean = false,

    var categories: List<Category> = emptyList(),
    var currentCategory: Category = Category(),

    var sheetContent: SheetContent = SheetContent.DEFAULT
)

enum class SheetContent {
    DEFAULT, CATEGORY, VARIATIONS, STOCK, IMAGES
}


sealed class EditProductEvent {
    object BackToShop : EditProductEvent()
    object ClearFocus : EditProductEvent()
    data class ShowSnackBar(val message: String): EditProductEvent()
    data class SetShowBottomSheet(val status: Boolean): EditProductEvent()
}
