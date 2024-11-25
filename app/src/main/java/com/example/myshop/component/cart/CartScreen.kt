package com.example.myshop.component.cart

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.util.Screen
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CartEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is CartEvent.SetShowBottomSheet -> {
                    if (event.status) sheetState.show() else sheetState.hide()
                }

                is CartEvent.NavigateToProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodedJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodedJson")
                }

                is CartEvent.CheckOut -> {
                    val ordersJson = Gson().toJson(event.orders)
                    val encodeJson = Uri.encode(ordersJson)
                    navController.navigate(Screen.CheckOut.route + "?orders=$encodeJson")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    CartContent(
        sheetState = sheetState,
        state = state,
        onCartChange = viewModel::onCartChange,
        deleteOrderInCart = viewModel::deleteOrderInCart,
        onEditOrderChange = viewModel::onEditOrderChange,
        editOrder = viewModel::editOrder,
        onBack = { viewModel.onEvent(CartEvent.BackToPrevScreen) },
        checkOut = { viewModel.onEvent(CartEvent.CheckOut(it)) },
        viewProduct = { viewModel.onEvent(CartEvent.NavigateToProduct(it)) },
        setShowBottomSheet = { viewModel.onEvent(CartEvent.SetShowBottomSheet(it)) },
    )
}
