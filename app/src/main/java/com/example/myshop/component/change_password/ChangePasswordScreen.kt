package com.example.myshop.component.change_password

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChangePasswordScreen(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ChangePasswordEvent.HideKeyBoard -> {
                    focusManager.clearFocus()
                }

                is ChangePasswordEvent.BackToProfile -> {
                    navHostController.popBackStack()
                }

                is ChangePasswordEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ChangePasswordContent(
        state = state,
        onOldPasswordChange = viewModel::onOldPasswordChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onToggleVisibleOldPassword = viewModel::onToggleVisibleOldPassword,
        onToggleVisibleNewPassword = viewModel::onToggleVisibleNewPassword,
        onToggleVisibleConfirmPassword = viewModel::onToggleVisibleConfirmPassword,
        onBack = { viewModel.onEvent(ChangePasswordEvent.BackToProfile) },
        onHideKeyBoard = { viewModel.onEvent(ChangePasswordEvent.HideKeyBoard) },
        onChangePassword = viewModel::changePassword
    )
}
