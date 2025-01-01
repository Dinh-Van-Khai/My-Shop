package com.example.myshop.component.my_likes

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.model.Like
import com.example.myshop.model.User
import com.example.myshop.ui.theme.Primary


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun MyLikesContent(
    state: MyLikesState = MyLikesState(),
    onUnlikeShop: (Like) -> Unit = {},
    viewShop: (User) -> Unit = {},
    onBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                shadowElevation = 16.dp,
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
                        text = "My Liked Shop",
                        fontSize = 20.sp,
                        color = Primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (state.likes.isEmpty()) {
                item {
                    Text(
                        text = "You have not liked any shops yet",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 10.dp),
                    )
                }
            }
            items(count = state.likes.size, key = {state.likes[it].lid }) {
                val like = state.likes[it]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewShop(like.user)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = like.user.profilePicture,
                        contentDescription = "Shop's Profile picture",
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 8.dp)
                            .size(55.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = like.user.name,
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = "Icon Location",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = like.user.address,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 15.dp),
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .border(1.dp, Color.Red, RoundedCornerShape(5.dp))
                            .clickable(onClick = { onUnlikeShop(like) })
                    ) {
                        Text(
                            text = "Unlike",
                            color = Color.Red,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}