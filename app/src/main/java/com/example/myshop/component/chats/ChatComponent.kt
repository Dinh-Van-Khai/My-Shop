package com.example.myshop.component.chats

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.model.Message
import com.example.myshop.model.User
import com.example.myshop.ui.theme.Primary
import java.text.SimpleDateFormat


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChatsContent(
    state: ChatsState = ChatsState(),
    onBack: () -> Unit = {},
    onGoToMessage: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Icon back",
                        tint = Primary,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(10.dp)
                            .clip(CircleShape)
                            .size(30.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = onBack
                            )
                    )
                    Text(
                        text = "Chats",
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(count = state.messages.size, key = {state.messages[it].mid }) {
                val message = state.messages[it]
                ItemChats(
                    modifier = Modifier.fillMaxWidth(),
                    message = message,
                    user = state.user,
                    isRead = !message.read && message.to.uid == state.user.uid,
                    onClick = {
                        val othersId = if(state.user.uid == message.from.uid) {
                            message.to.uid
                        } else {
                            message.from.uid
                        }
                        onGoToMessage(othersId)
                    }
                )
                Divider(Modifier.fillMaxWidth())
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun ItemChats(
    modifier: Modifier = Modifier,
    message: Message,
    user: User,
    isRead: Boolean = false,
    onClick: () -> Unit = {}
) {
    val simpleDateFormat = SimpleDateFormat("MMM dd")
    Row(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if(user.uid == message.from.uid) message.to.profilePicture else message.from.profilePicture,
            contentDescription = "Profile picture of others",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clip(CircleShape)
                .size(60.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(end = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if(user.uid == message.from.uid) message.to.name else message.from.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                if (isRead) {
                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Blue)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message.text.ifBlank { "Sent ${message.images.size} images" },
                    maxLines = 1,
                    fontWeight = if (isRead) FontWeight.Medium else FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = message.date?.let { " - ${simpleDateFormat.format(it)}" } ?: "",
                    maxLines = 1,
                    fontWeight = if (isRead) FontWeight.Medium else FontWeight.Normal,
                )
            }
        }
    }
}