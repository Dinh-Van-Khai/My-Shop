package com.example.myshop.component.product

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.util.Screen
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    pid: String,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState()
    viewModel.getInformationProduct(pid)
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ProductEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is ProductEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is ProductEvent.NavigateToLogIn -> {
                    navController.navigate(Screen.LogIn.route)
                }

                is ProductEvent.NavigateToCart -> {
                    navController.navigate(Screen.Cart.route)
                }

                is ProductEvent.NavigateToChats -> {
                    navController.navigate(Screen.Chats.route)
                }

                is ProductEvent.NavigateToMessage -> {
                    navController.navigate(Screen.Message.route + "?othersId=${event.otherId}")
                }

                is ProductEvent.CheckOut -> {
                    val ordersJson = Gson().toJson(event.orders)
                    val encodeJson = Uri.encode(ordersJson)
                    navController.navigate(Screen.CheckOut.route + "?orders=$encodeJson")
                }

                is ProductEvent.ViewShop -> {
                    navController.navigate(Screen.Shop.route + "?uid=${event.uid}")
                }

                is ProductEvent.ViewProduct -> {
                    val productJson = Gson().toJson(event.product)
                    val encodedJson = Uri.encode(productJson)
                    navController.navigate(Screen.Product.route + "?product=$encodedJson")
                }

                is ProductEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is ProductEvent.SetShowBottomSheet -> {
                    if (event.status) sheetState.show() else sheetState.hide()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ProductContent(
        sheetState = sheetState,
        state = state,
        setFirstItemOffset = viewModel::setFirstItemOffset,
        onClickAddToCart = viewModel::onClickAddToCart,
        onClickBuyNow = viewModel::onClickBuyNow,
        onClickCart = viewModel::onClickCart,
        onClickChatIcon = viewModel::onClickChats,
        onChangeOrder = viewModel::onChangeOrder,
        addToCart = viewModel::onAddToCart,
        onClickChatWithShop = {viewModel.onEvent(ProductEvent.NavigateToMessage(it))},
        checkOut = { viewModel.onEvent(ProductEvent.CheckOut(it)) },
        setShowBottomSheet = { viewModel.onEvent(ProductEvent.SetShowBottomSheet(it)) },
        viewShop = { viewModel.onEvent(ProductEvent.ViewShop(state.product.user.uid)) },
        viewProduct = { viewModel.onEvent(ProductEvent.ViewProduct(it)) },
        onBack = { viewModel.onEvent(ProductEvent.BackToPrevScreen) },
    )
}
