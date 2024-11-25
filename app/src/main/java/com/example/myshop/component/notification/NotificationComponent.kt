package com.example.myshop.component.notification

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.model.Notification
import com.example.myshop.util.dateFormat
import java.util.concurrent.TimeUnit

@Composable
fun NotificationContent(
    state: NotificationState = NotificationState(),
    markAllAsRead: () -> Unit = {},
    onClickItem: (Notification) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(bottom = 50.dp),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                shadowElevation = 8.dp,
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 22.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_checkmark),
                        contentDescription = "Icon Double Tick",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp)
                            .size(30.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = markAllAsRead
                            )
                    )
                }
            }
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(count = state.notifications.size, key = {state.notifications[it].nid }) {
                val notification = state.notifications[it]
                ItemNotifications(
                    modifier = Modifier.fillMaxWidth(),
                    notification = notification,
                    onClick = onClickItem
                )
                HorizontalDivider(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ItemNotifications(
    modifier: Modifier = Modifier,
    notification: Notification,
    onClick: (Notification) -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(if (notification.read) Color.White else Color(0xFFAFEAFF))
            .clickable { onClick(notification) }
    ) {
        AsyncImage(
            model = notification.image,
            contentDescription = "Notification Image",
            modifier = Modifier
                .padding(10.dp)
                .size(80.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        ) {
            Text(
                text = notification.title,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = notification.message,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            notification.date?.let { date ->
                val time = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - date.time)
                Text(
                    text = when {
                        time < 60 -> "$time minutes ago"
                        time <= 60 * 24 -> "${time / 60L} hours ago"
                        else -> dateFormat.format(date)
                    },
                    fontSize = 14.sp
                )
            }
        }
    }
}