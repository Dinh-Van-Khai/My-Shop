package com.example.myshop.component.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.User
import com.example.myshop.util.OrderStatus
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.EmailAuthProvider
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

@Suppress("DEPRECATION")
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val appContext: Application
) : BaseViewModel<ProfileState, ProfileEvent>(ProfileState(isLogged = auth.currentUser != null)) {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private val tokensCollectionRef = Firebase.firestore.collection("tokens")
    private val ordersCollectionRef = Firebase.firestore.collection("orders")

    private var userInformationListener: ListenerRegistration? = null
    private var numberOfCartListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener {
            getInformationUser()
            getNumberOfCart()
        }
    }

    private fun getInformationUser() {
        _state.update { it.copy(isLogged = auth.currentUser != null) }
        auth.currentUser?.run {
            val isEmailAuth = providerData.any { userInfo ->
                userInfo.providerId == EmailAuthProvider.PROVIDER_ID
            }
            _state.update { it.copy(isEmailAuth = isEmailAuth) }

            //Observer User Information
            userInformationListener = usersCollectionRef
                .document(uid)
                .addSnapshotListener { documentSnapshot, error ->
                    error?.let { e ->
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    documentSnapshot?.let { snapShot ->
                        snapShot.toObject<User>()?.run {
                            _state.update {
                                it.copy(
                                    email = email,
                                    displayName = name,
                                    photoUrl = profilePicture
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun getNumberOfCart() {
        numberOfCartListener?.remove()

        if (auth.currentUser == null) {
            _state.update { it.copy(numberOfCart = 0) }
            return
        }

        auth.currentUser?.run {
            numberOfCartListener = ordersCollectionRef
                .where(
                    Filter.and(
                        Filter.equalTo("customer.uid", uid),
                        Filter.equalTo("status", OrderStatus.ADDED_TO_CART)
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

    fun onClickCart() {
        if (auth.currentUser != null) {
            onEvent(ProfileEvent.NavigateToCart)
        } else {
            onEvent(ProfileEvent.NavigateToLogIn)
        }
    }

    fun onClickChats() {
        if (auth.currentUser != null) {
            onEvent(ProfileEvent.NavigateToChats)
        } else {
            onEvent(ProfileEvent.NavigateToLogIn)
        }
    }

    fun onClickMyAccount() {
        if (auth.currentUser != null) {
            onEvent(ProfileEvent.NavigateToUserScreen)
        } else {
            onEvent(ProfileEvent.NavigateToLogIn)
        }
    }

    fun onClickMyShop() {
        if (auth.currentUser != null) {
            onEvent(ProfileEvent.NavigateToShopScreen)
        } else {
            onEvent(ProfileEvent.NavigateToLogIn)
        }
    }

    fun onClickMyLikes() {
        if (auth.currentUser != null) {
            onEvent(ProfileEvent.NavigateToMyLikesScreen)
        } else {
            onEvent(ProfileEvent.NavigateToLogIn)
        }
    }

    fun onClickMyPurchase() {
        if (auth.currentUser != null) {
            onEvent(ProfileEvent.NavigateToMyPurchase)
        } else {
            onEvent(ProfileEvent.NavigateToLogIn)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.currentUser?.run {
                val tokenDocumentRef = tokensCollectionRef.document(uid)
                tokenDocumentRef.delete().await()
                userInformationListener?.remove()
                LoginManager.getInstance().logOut()
                Identity.getSignInClient(appContext).signOut()
                auth.signOut()
                _state.update { ProfileState() }
            }
        }
    }
}

data class ProfileState(
    var isLogged: Boolean = false,
    var email: String = "",
    var displayName: String = "",
    var photoUrl: String = "",
    var isEmailAuth: Boolean = false,
    var numberOfCart: Int = 0,
    var numberOfChats: Int = 0
)

sealed class ProfileEvent {
    data object NavigateToLogIn : ProfileEvent()
    data object NavigateToSignUp : ProfileEvent()
    data object NavigateToCart : ProfileEvent()
    data object NavigateToChats : ProfileEvent()
    data object NavigateToUserScreen : ProfileEvent()
    data object NavigateToMyPurchase : ProfileEvent()
    data object NavigateToMyLikesScreen : ProfileEvent()
    data object NavigateToPasswordScreen : ProfileEvent()
    data object NavigateToShopScreen : ProfileEvent()
}
