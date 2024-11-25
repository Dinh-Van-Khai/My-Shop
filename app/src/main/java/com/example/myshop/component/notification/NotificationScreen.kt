package com.example.myshop.component.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationScreen(
    navController: NavHostController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when(event) {
                is NotificationsEvent.NavigateToRoute -> {
                    navController.navigate(event.route)
                }
            }
        }
    }
    val state = viewModel.state.collectAsStateWithLifecycle().value
    NotificationContent(
        state = state,
        markAllAsRead = viewModel::markAllAsRead,
        onClickItem = viewModel::onClickNotification
    )
}