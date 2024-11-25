package com.example.myshop.component.profile

import android.app.Application
import com.example.myshop.component.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(private val appContext: Application) : BaseViewModel<ProfileState, ProfileEvent>(ProfileState(isLogged = false)) {

    init {
        getInformationUser()
        getNumberOfCart()
    }

    private fun getInformationUser() {
        _state.update { it.copy(
            isLogged = true,
            isEmailAuth = true,
            email = "abc@gmail.com",
            displayName = "abc",
            photoUrl = "https://scontent.fhan3-3.fna.fbcdn.net/v/t39.30808-1/465891449_571483215572739_9153817871065029220_n.jpg?stp=c185.554.1000.1000a_cp0_dst-jpg_s40x40&_nc_cat=1&ccb=1-7&_nc_sid=f4b9fd&_nc_ohc=WBxzSge95MQQ7kNvgHrQ7AQ&_nc_zt=24&_nc_ht=scontent.fhan3-3.fna&_nc_gid=ANFG0KZJ_OgVfY5UPRhB0dw&oh=00_AYDYV-PMJVI0ZRR_sfbXap_cH1eKkEigu0lzFC3-f04NBA&oe=67413F44"
        ) }
    }

    private fun getNumberOfCart() {
        _state.update { it.copy(numberOfCart = 3) }
    }

    fun onClickCart() {
        onEvent(ProfileEvent.NavigateToCart)
    }

    fun onClickChats() {
        onEvent(ProfileEvent.NavigateToChats)
    }

    fun onClickMyAccount() {
        onEvent(ProfileEvent.NavigateToUserScreen)
    }

    fun onClickMyShop() {
        onEvent(ProfileEvent.NavigateToShopScreen)
    }

    fun onClickMyLikes() {
        onEvent(ProfileEvent.NavigateToMyLikesScreen)
    }

    fun onClickMyPurchase() {
        onEvent(ProfileEvent.NavigateToMyPurchase)
    }

    fun signOut() {
        _state.update { ProfileState() }
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
    object NavigateToLogIn : ProfileEvent()
    object NavigateToSignUp : ProfileEvent()
    object NavigateToCart : ProfileEvent()
    object NavigateToChats : ProfileEvent()
    object NavigateToUserScreen : ProfileEvent()
    object NavigateToMyPurchase : ProfileEvent()
    object NavigateToMyLikesScreen : ProfileEvent()
    object NavigateToPasswordScreen : ProfileEvent()
    object NavigateToShopScreen : ProfileEvent()
}
