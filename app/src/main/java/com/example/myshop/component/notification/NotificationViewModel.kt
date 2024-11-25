package com.example.myshop.component.notification

import android.app.Application
import android.app.NotificationManager
import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Notification
import com.example.myshop.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val appContext: Application
) : BaseViewModel<NotificationState, NotificationsEvent>(NotificationState()) {

    init {
        getAllNotification()
        markAllAsSeen()
    }

    private fun getAllNotification() {
        val notifications = listOf(
            Notification(
                date = Date(),
                to = "001",
                read = false,
                seen = true,
                nid = "002",
                image = "https://i.ytimg.com/vi/UFkEfcYKlSk/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLCP0KyEMDLCTLXap52VAOlmpMn8Sw",
                route = Screen.MyShopPurchases.route,
                title = "002",
                message = "Your product has been ordered"
            ),
            Notification(
                date = Date(),
                to = "001",
                read = false,
                seen = true,
                nid = "001",
                image = "https://i.ytimg.com/vi/rRUzJJ9IV4s/hqdefault.jpg?sqp=-oaymwExCOADEI4CSFryq4qpAyMIARUAAIhCGAHwAQH4Af4JgALQBYoCDAgAEAEYfyA3KCkwDw==&rs=AOn4CLCVIcZ022gQEo-Tzl0ybsUkzD9Caw",
                route = Screen.MyShopPurchases.route,
                title = "001",
                message = "Your product has been ordered"
            ),
            Notification(
                date = Date(),
                to = "001",
                read = false,
                seen = true,
                nid = "003",
                image = "https://i.ytimg.com/vi/UFkEfcYKlSk/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLCP0KyEMDLCTLXap52VAOlmpMn8Sw",
                route = Screen.MyShopPurchases.route,
                title = "003",
                message = "Your product has been ordered"
            ),

        )
        _state.update { it.copy(notifications = notifications) }
    }

    private fun markAllAsSeen() {

        val notificationManager = appContext.getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()
    }

    fun markAllAsRead() {
    }

    fun onClickNotification(notification: Notification) {
        viewModelScope.launch {
            if (!notification.read) {
            }
            onEvent(NotificationsEvent.NavigateToRoute(notification.route))
        }
    }

}

data class NotificationState(
    var notifications: List<Notification> = emptyList()
)

sealed class NotificationsEvent {
    data class NavigateToRoute(val route: String) : NotificationsEvent()
}