package com.example.myshop.component.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.util.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ProfileEvent.NavigateToCart -> {
                    navController.navigate(Screen.Cart.route)
                }

                is ProfileEvent.NavigateToChats -> {
                    navController.navigate(Screen.Chats.route)
                }

                is ProfileEvent.NavigateToLogIn -> {
                    navController.navigate(Screen.LogIn.route)
                }

                is ProfileEvent.NavigateToSignUp -> {
                    navController.navigate(Screen.SignUp.route)
                }

                is ProfileEvent.NavigateToUserScreen -> {
                    navController.navigate(Screen.User.route)
                }

                is ProfileEvent.NavigateToMyPurchase -> {
                    navController.navigate(Screen.MyPurchase.route)
                }

                is ProfileEvent.NavigateToMyLikesScreen -> {
                    navController.navigate(Screen.MyLikes.route)
                }

                is ProfileEvent.NavigateToPasswordScreen -> {
                    navController.navigate(Screen.ChangePassword.route)
                }

                is ProfileEvent.NavigateToShopScreen -> {
                    navController.navigate(Screen.MyShop.route)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ProfileContent(
        state = state,
        onClickCart = viewModel::onClickCart,
        onClickChats = viewModel::onClickChats,
        onClickMyAccount = viewModel::onClickMyAccount,
        onClickMyShop = viewModel::onClickMyShop,
        onClickMyPurchase = viewModel::onClickMyPurchase,
        onClickMyLikes = viewModel::onClickMyLikes,
        onSignOut = viewModel::signOut,
        onClickChangePassword = { viewModel.onEvent(ProfileEvent.NavigateToPasswordScreen) },
        onLogIn = { viewModel.onEvent(ProfileEvent.NavigateToLogIn) },
        onSignUp = { viewModel.onEvent(ProfileEvent.NavigateToSignUp) },
    )
}
