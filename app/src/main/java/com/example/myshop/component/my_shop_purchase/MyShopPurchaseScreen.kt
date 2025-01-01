package com.example.myshop.component.my_shop_purchase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyShopPurchaseScreen(
    navController: NavHostController,
    viewModel: MyShopPurchaseViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MyShopPurchaseEvent.BackToMyShopScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MyShopPurchaseContent(
        state = state,
        setTab = viewModel::setNewTab,
        onConfirmOrder = viewModel::confirmOrder,
        onCancelOrder = viewModel::cancelOrder,
        onConfirmShipped = viewModel::confirmShipped,
        onBack = { viewModel.onEvent(MyShopPurchaseEvent.BackToMyShopScreen) },
    )
}
