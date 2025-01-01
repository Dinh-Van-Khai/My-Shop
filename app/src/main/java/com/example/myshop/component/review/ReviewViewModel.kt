package com.example.myshop.component.review

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.model.Review
import com.example.myshop.util.getFileExtension
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val appContext: Application
) : BaseViewModel<ReviewState, ReviewEvent>(ReviewState()) {

    private val productsCollectionRef = Firebase.firestore.collection("products")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private val productsStorageRef = Firebase.storage.reference.child("products")

    fun setOrder(order: Order) {
        _state.update { it.copy(order = order) }
    }

    fun onRateChange(newRate: Int) {
        _state.update { it.copy(rate = newRate) }
    }

    fun onCommentChange(newValue: String) {
        _state.update { it.copy(comment = newValue) }
    }

    fun onListImageCommentChange(images: List<Uri>) {
        _state.update { it.copy(listImagesComment = images) }
    }

    fun sendReview() {
        onEvent(ReviewEvent.ClearFocus)
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                var tempListImage = emptyList<String>()
                val productsRef = productsStorageRef.child("/${state.value.order.product.pid}/images-review")
                state.value.listImagesComment.forEach { uri ->
                    val fileExtension = getFileExtension(uri, appContext.contentResolver)
                    val randomFileName =
                        "${UUID.randomUUID()}-${System.currentTimeMillis()}.$fileExtension"
                    val productImageRef = productsRef.child(randomFileName)
                    val taskSnapshot = productImageRef.putFile(uri).await()

                    val task = taskSnapshot.task
                    if (task.isComplete) {
                        val url = productImageRef.downloadUrl.await()
                        val path = url?.toString() ?: ""
                        tempListImage = tempListImage + path
                    }
                }
                val review = Review(
                    user = state.value.order.customer,
                    time = Date(System.currentTimeMillis()),
                    rate = state.value.rate,
                    comment = state.value.comment,
                    images = tempListImage
                )
                val isSuccessful = Firebase.firestore.runTransaction<Boolean> { transaction ->

                    val productDocument = productsCollectionRef.document(state.value.order.product.pid)
                    val orderDocument = ordersCollectionRef.document(state.value.order.oid)

                    val snapShot = transaction.get(productDocument)
                    snapShot.toObject<Product>()?.run {
                        transaction.update(productDocument, "reviews", reviews + review)
                        transaction.update(orderDocument, "reviewed", true)
                        return@runTransaction true
                    }
                    return@runTransaction false
                }.await()

                if (isSuccessful) {
                    _state.update { it.copy(isLoading = false) }
                    onEvent(ReviewEvent.BackToPrevScreen)
                } else {
                    _state.update { it.copy(isLoading = false) }
                    onEvent(ReviewEvent.ShowSnackBar("Error! Can not send review"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isLoading = false) }
                onEvent(ReviewEvent.ShowSnackBar(e.message ?: "Error! Can not send review"))
            }
        }
    }
}

data class ReviewState(
    var order: Order = Order(),
    var rate: Int = 0,
    var comment: String = "",
    var listImagesComment: List<Uri> = emptyList(),

    var isLoading: Boolean = false
)

sealed class ReviewEvent {
    object BackToPrevScreen : ReviewEvent()
    object ClearFocus: ReviewEvent()
    data class ShowSnackBar(val message: String): ReviewEvent()
    data class SetShowBottomSheet(val status: Boolean) : ReviewEvent()

}
