package com.example.myshop.component.my_purchase

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
fun MyPurchaseScreen(
    navController: NavHostController,
    viewModel: MyPurchaseViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MyPurchaseEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is MyPurchaseEvent.ViewProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodeJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodeJson")
                }

                is MyPurchaseEvent.CheckOut -> {
                    val ordersJson = Gson().toJson(event.orders)
                    val encodeJson = Uri.encode(ordersJson)
                    navController.navigate(Screen.CheckOut.route + "?orders=$encodeJson")
                }

                is MyPurchaseEvent.Review -> {
                    val orderJson = Gson().toJson(event.order)
                    val encodeJson = Uri.encode(orderJson)
                    navController.navigate(Screen.Review.route + "?order=$encodeJson")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MyPurchaseContent(
        state = state,
        setTab = viewModel::setNewTab,
        onCancelOrder = viewModel::cancelOrder,
        onBack = { viewModel.onEvent(MyPurchaseEvent.BackToPrevScreen) },
        onViewProduct = { viewModel.onEvent(MyPurchaseEvent.ViewProduct(it)) },
        onBuyAgain = { viewModel.onEvent(MyPurchaseEvent.CheckOut(listOf(it.copy(oid = "")))) },
        onReview = { viewModel.onEvent(MyPurchaseEvent.Review(it)) }
    )
}

