package com.example.myshop.component.my_shop

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.model.Product
import com.example.myshop.util.Screen
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyShopScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: MyShopViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MyShopEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is MyShopEvent.BackToProfile -> {
                    navController.popBackStack()
                }

                is MyShopEvent.AddProduct -> {
                    val jsonProduct = Gson().toJson(Product())
                    navController.navigate(
                        Screen.EditProduct.route +
                                "?product=$jsonProduct" +
                                "&label=Add Product" +
                                "&button=Publish"
                    )
                }

                is MyShopEvent.EditProduct -> {
                    val jsonProduct = Gson().toJson(event.product)
                    val encodeJson = Uri.encode(jsonProduct)
                    navController.navigate(
                        Screen.EditProduct.route +
                                "?product=$encodeJson" +
                                "&label=Edit Product" +
                                "&button=Confirm"
                    )
                }

                is MyShopEvent.NavigateToMyShopPurchases -> {
                    navController.navigate(Screen.MyShopPurchases.route)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MyShopContent(
        state = state,
        onDeleteProduct = viewModel::deleteProduct,
        editProduct = { viewModel.onEvent(MyShopEvent.EditProduct(it)) },
        onAddProduct = { viewModel.onEvent(MyShopEvent.AddProduct) },
        onClickMyShopPurchases = {viewModel.onEvent(MyShopEvent.NavigateToMyShopPurchases)},
        onBack = { viewModel.onEvent(MyShopEvent.BackToProfile) },
    )
}
