package com.example.myshop.component.check_out

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.model.Order
import kotlinx.coroutines.flow.collectLatest


@Composable
fun CheckOutScreen(
    orders: List<Order>,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: CheckOutViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        viewModel.onOrdersChange(orders)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CheckOutEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is CheckOutEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is CheckOutEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    CheckOutContent(
        state = state,
        onOrdersChange = viewModel::onOrdersChange,
        placeOrder = viewModel::placeOrder,
        onClearFocus = { viewModel.onEvent(CheckOutEvent.ClearFocus) },
        onBack = { viewModel.onEvent(CheckOutEvent.BackToPrevScreen) }
    )
}
