package com.example.myshop.component.change_password

import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : BaseViewModel<ChangePasswordState, ChangePasswordEvent>(ChangePasswordState()) {

    fun onOldPasswordChange(newValue: String) {
        if (newValue.length < 40) {
            _state.update { it.copy(oldPassword = newValue) }
        }
    }

    fun onNewPasswordChange(newValue: String) {
        if (newValue.length < 40) {
            _state.update { it.copy(newPassword = newValue) }
        }
    }

    fun onConfirmPasswordChange(newValue: String) {
        if (newValue.length < 40) {
            _state.update { it.copy(confirmPassword = newValue) }
        }
    }

    fun onToggleVisibleOldPassword() {
        _state.update { it.copy(visibleOldPassword = !it.visibleOldPassword) }
    }

    fun onToggleVisibleNewPassword() {
        _state.update { it.copy(visibleNewPassword = !it.visibleNewPassword) }
    }

    fun onToggleVisibleConfirmPassword() {
        _state.update { it.copy(visibleConfirmPassword = !it.visibleConfirmPassword) }
    }

    fun changePassword() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            onEvent(ChangePasswordEvent.HideKeyBoard)
            delay(300L)
            if (state.value.newPassword.length < 15) {
                _state.update { it.copy(isLoading = false) }
                onEvent(ChangePasswordEvent.ShowSnackBar("New Password must be longer than 15 character"))
                return@launch
            } else if (state.value.confirmPassword != state.value.newPassword) {
                _state.update { it.copy(isLoading = false) }
                onEvent(ChangePasswordEvent.ShowSnackBar("Confirm password does not match"))
                return@launch
            }
            auth.currentUser?.run {
                try {
                    val credential = EmailAuthProvider.getCredential(email ?: "", state.value.oldPassword)
                    val taskAuthenticate = reauthenticate(credential)
                    taskAuthenticate.await()
                    if (taskAuthenticate.isSuccessful) {
                        val taskChange = updatePassword(state.value.newPassword)
                        taskChange.await()
                        if (taskChange.isSuccessful) {
                            _state.update { it.copy(isLoading = false) }
                            onEvent(ChangePasswordEvent.BackToProfile)
                            onEvent(ChangePasswordEvent.ShowSnackBar("Change password successful"))
                            return@launch
                        } else {
                            _state.update { it.copy(isLoading = false) }
                            onEvent(ChangePasswordEvent.ShowSnackBar("Can't change password"))
                            return@launch
                        }
                    } else {
                        onEvent(ChangePasswordEvent.ShowSnackBar("Wrong old password"))
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false) }
                    onEvent(ChangePasswordEvent.ShowSnackBar(e.message.toString()))
                    return@launch
                }
            }
        }
    }
}

data class ChangePasswordState(
    var oldPassword: String = "",
    var newPassword: String = "",
    var confirmPassword: String = "",
    var visibleOldPassword: Boolean = false,
    var visibleNewPassword: Boolean = false,
    var visibleConfirmPassword: Boolean = false,
    var isLoading: Boolean = false,
)

sealed class ChangePasswordEvent {
    object HideKeyBoard: ChangePasswordEvent()
    object BackToProfile: ChangePasswordEvent()
    data class ShowSnackBar(val message: String): ChangePasswordEvent()
}