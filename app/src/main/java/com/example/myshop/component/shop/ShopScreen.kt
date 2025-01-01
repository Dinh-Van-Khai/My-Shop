package com.example.myshop.component.shop

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.util.Screen
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ShopScreen(
    uid: String,
    navController: NavHostController,
    viewModel: ShopViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getShop(uid)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ShopEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is ShopEvent.NavigateToLogIn -> {
                    navController.navigate(Screen.LogIn.route)
                }

                is ShopEvent.NavigateToCart -> {
                    navController.navigate(Screen.Cart.route)
                }

                is ShopEvent.NavigateToChats -> {
                    navController.navigate(Screen.Chats.route)
                }

                is ShopEvent.NavigateToMessage -> {
                    navController.navigate(Screen.Message.route + "?othersId=${event.otherId}")
                }

                is ShopEvent.ViewProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodedJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodedJson")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ShopContent(
        state = state,
        likeShop = viewModel::likeShop,
        unlikeShop = viewModel::unlikeShop,
        onClickCart = viewModel::onClickCart,
        onClickChats = viewModel::onClickChats,
        onClickChatWithShop = {viewModel.onEvent(ShopEvent.NavigateToMessage(it))},
        onBack = { viewModel.onEvent(ShopEvent.BackToPrevScreen) },
        viewProduct = { viewModel.onEvent(ShopEvent.ViewProduct(it)) }
    )
}
