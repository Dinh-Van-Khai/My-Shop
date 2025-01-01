package com.example.myshop.component.review

import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.model.Order
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun ReviewScreen(
    order: Order,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(Unit) {
        viewModel.setOrder(order)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ReviewEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is ReviewEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is ReviewEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is ReviewEvent.SetShowBottomSheet -> {
                    if (event.status) sheetState.show() else sheetState.hide()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ReviewContent(
        sheetState = sheetState,
        state = state,
        onRateChange = viewModel::onRateChange,
        onCommentChange = viewModel::onCommentChange,
        onListImageCommentChange = viewModel::onListImageCommentChange,
        onSendReview = viewModel::sendReview,
        setShowBottomSheet = { viewModel.onEvent(ReviewEvent.SetShowBottomSheet(it)) },
        clearFocus = { viewModel.onEvent(ReviewEvent.ClearFocus) },
        onBack = { viewModel.onEvent(ReviewEvent.BackToPrevScreen) }
    )
}

