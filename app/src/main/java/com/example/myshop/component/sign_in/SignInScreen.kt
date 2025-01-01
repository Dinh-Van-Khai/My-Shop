package com.example.myshop.component.sign_in

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.util.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.logInWithGoogle(result.data)
            } else {
                viewModel.onEvent(SignInEvent.ShowSnackBar("Can not log in with Google"))
            }
        }
    )
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is SignInEvent.HideHeyBoard -> {
                    focusManager.clearFocus()
                }
                is SignInEvent.NavigateToSignUp -> {
                    navController.navigate(Screen.SignUp.route)
                }
                is SignInEvent.BackToProfile -> {
                    navController.popBackStack()
                }
                is SignInEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    SignInContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onToggleVisiblePassword = viewModel::onToggleVisiblePassword,
        onHideKeyBoard = { viewModel.onEvent(SignInEvent.HideHeyBoard) },
        onBack = { viewModel.onEvent(SignInEvent.BackToProfile) },
        onSignIn = viewModel::logIn,
        onSignInWithGoogle = {
            scope.launch {
                val signInIntentSender = viewModel.getSignInIntentSender()
                launcher.launch(
                    IntentSenderRequest
                        .Builder(signInIntentSender ?: return@launch)
                        .build()
                )
            }
        },
        onSignInWithFacebook = { viewModel.logInWithFacebook(context) },
        onCreateNewAccount = { viewModel.onEvent(SignInEvent.NavigateToSignUp) },
    )
}

