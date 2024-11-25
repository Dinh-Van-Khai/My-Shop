package com.example.myshop.component.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myshop.R
import com.example.myshop.ui.theme.Primary
import com.example.myshop.ui.theme.shimmerEffect

@Composable
fun ProfileContent(
    state: ProfileState = ProfileState(),
    onClickCart: () -> Unit = {},
    onClickChats: () -> Unit = {},
    onClickMyAccount: () -> Unit = {},
    onClickMyShop: () -> Unit = {},
    onClickMyPurchase: () -> Unit = {},
    onClickMyLikes: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onClickChangePassword: () -> Unit = {},
    onLogIn: () -> Unit = {},
    onSignUp: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onClickCart
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_cart),
                                contentDescription = "Cart icon",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(30.dp),
                                tint = Color.White
                            )
                            if (state.numberOfCart > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.numberOfCart.toString(),
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onClickChats
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_message),
                                contentDescription = "Message icon",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(30.dp),
                                tint = Color.White
                            )
                            if (state.numberOfChats > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.numberOfChats.toString(),
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = state.photoUrl.ifBlank { R.drawable.user },
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .size(80.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                        if (state.isLogged) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = state.displayName,
                                    maxLines = 1,
                                    fontSize = 20.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                                Text(
                                    text = state.email,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                            OutlinedButton(
                                onClick = onLogIn,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Primary
                                )
                            ) {
                                Text(text = "Log In")
                            }
                            OutlinedButton(
                                onClick = onSignUp,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Primary
                                )
                            ) {
                                Text(text = "Sign up")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            ItemProfile(
                icon = R.drawable.ic_profile_filled,
                label = "My Account",
                onClick = onClickMyAccount
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            if (state.isLogged && state.isEmailAuth) {
                ItemProfile(
                    icon = R.drawable.ic_password,
                    label = "Change password",
                    onClick = onClickChangePassword
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                )
            }
            ItemProfile(
                icon = R.drawable.ic_myshop,
                label = "My Shop",
                onClick = onClickMyShop
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemProfile(
                icon = R.drawable.ic_purchase,
                label = "My Purchases",
                onClick = onClickMyPurchase
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemProfile(
                icon = R.drawable.ic_love,
                label = "My Likes",
                onClick = onClickMyLikes
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            )
            ItemProfile(
                icon = R.drawable.ic_help,
                label = "Help Centre",
                onClick = {}
            )
            if (state.isLogged) {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Log out")
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun ItemProfile(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick, enabled = enable),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "Icon",
            modifier = Modifier
                .size(45.dp)
                .padding(horizontal = 10.dp)
        )
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = 1
        )
    }
}
