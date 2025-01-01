package com.example.myshop.component.chats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myshop.util.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChatsScreen(
    navController: NavHostController,
    viewModel: ChatsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ChatsEvent.BackToPrevScreen -> {
                    navController.popBackStack()
                }

                is ChatsEvent.NavigateToMessage -> {
                    navController.navigate(Screen.Message.route + "?othersId=${event.otherId}")
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    ChatsContent(
        state = state,
        onBack = { viewModel.onEvent(ChatsEvent.BackToPrevScreen) },
        onGoToMessage = { viewModel.onEvent(ChatsEvent.NavigateToMessage(it)) }
    )
}
