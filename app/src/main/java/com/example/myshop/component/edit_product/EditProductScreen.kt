package com.example.myshop.component.edit_product

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.model.Product
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun EditProductScreen(
    product: Product,
    label: String,
    button: String,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: EditProductViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(Unit) {
        viewModel.setInitProduct(product)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is EditProductEvent.BackToShop -> {
                    navController.popBackStack()
                }

                is EditProductEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is EditProductEvent.SetShowBottomSheet -> {
                    if (event.status) sheetState.show() else sheetState.hide()
                }

                is EditProductEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    EditProductContent(
        label = label,
        button = button,
        sheetState = sheetState,
        state = state,
        onChangeExistImages = viewModel::onChangeExistImages,
        onChangeImages = viewModel::onChangeImages,
        onChangeName = viewModel::onChangeName,
        onChangeDescription = viewModel::onChangeDescription,
        onChangePrice = viewModel::onChangePrice,
        onChangeCategory = viewModel::onChangeCategory,
        onChangeVariation = viewModel::onChangeVariations,
        setStocks = viewModel::setStocks,
        onAdd = viewModel::onAdd,
        setSheetContent = viewModel::setSheetContent,
        onBack = { viewModel.onEvent(EditProductEvent.BackToShop) },
        onClearFocus = { viewModel.onEvent(EditProductEvent.ClearFocus) },
        setShowBottomSheet = { viewModel.onEvent(EditProductEvent.SetShowBottomSheet(it)) },
    )
}

