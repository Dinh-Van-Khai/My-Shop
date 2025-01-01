package com.example.myshop.component.my_likes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.util.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyLikesScreen(
    navController: NavHostController,
    viewModel: MyLikesViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MyLikesEvent.ViewShop -> {
                    navController.navigate(Screen.Shop.route + "?uid=${event.uid}")
                }

                is MyLikesEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MyLikesContent(
        state = state,
        onUnlikeShop = viewModel::unlikeShop,
        viewShop = { viewModel.onEvent(MyLikesEvent.ViewShop(it.uid)) },
        onBack = { viewModel.onEvent(MyLikesEvent.BackToPrevScreen) }
    )
}

