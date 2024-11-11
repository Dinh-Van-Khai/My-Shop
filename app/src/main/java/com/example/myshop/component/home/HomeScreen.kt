package com.example.myshop.component.home

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
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is HomeEvent.NavigateToLogIn -> {
                    navController.navigate(Screen.LogIn.route)
                }

                is HomeEvent.NavigateToCart -> {
                    navController.navigate(Screen.Cart.route)
                }

                is HomeEvent.NavigateToChats -> {
                    navController.navigate(Screen.Chats.route)
                }

                is HomeEvent.NavigateToProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodedJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodedJson")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    HomeContent(
        state = state,
        setCurrentCategory = viewModel::setCurrentCategory,
        onSearchTextChange = viewModel::onSearchTextChange,
        onClickCart = viewModel::onClickCart,
        onClickChats = viewModel::onClickChats,
        onClickProduct = { viewModel.onEvent(HomeEvent.NavigateToProduct(it)) },
    )
}