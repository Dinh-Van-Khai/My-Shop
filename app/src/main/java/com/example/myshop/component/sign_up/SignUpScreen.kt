package com.example.myshop.component.sign_up

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is SignUpEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }
                is SignUpEvent.HideKeyBoard -> {
                    focusManager.clearFocus()
                }
                is SignUpEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    SignUpContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onToggleVisiblePassword = viewModel::onToggleVisiblePassword,
        onToggleVisibleConfirmPassword = viewModel::onToggleVisibleConfirmPassword,
        onBack = { viewModel.onEvent(SignUpEvent.BackToPrevScreen) },
        onHideKeyBoard = { viewModel.onEvent(SignUpEvent.HideKeyBoard) },
        onSignUp = viewModel::onSignIn
    )
}

