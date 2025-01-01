package com.example.myshop.component.message

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MessageScreen(
    othersId: String,
    navController: NavHostController,
    viewModel: MessageViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.getOthers(othersId)
        viewModel.getMessages(othersId)
    }
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MessageEvent.ClearFocus -> {
                    focusManager.clearFocus()
                }

                is MessageEvent.ScrollToFirstItem -> {
                    listState.animateScrollToItem(0)
                }

                is MessageEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MessageContent(
        listState = listState,
        state = state,
        onTextChange = viewModel::onTextChange,
        onImagesChange = viewModel::onImagesChange,
        onSendMessage = viewModel::onSendMessage,
        onBack = { viewModel.onEvent(MessageEvent.BackToPrevScreen) },
        onClearFocus = { viewModel.onEvent(MessageEvent.ClearFocus) },
    )
}
