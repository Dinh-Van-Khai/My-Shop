package com.example.myshop.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myshop.R
import com.example.myshop.component.home.HomeScreen
import com.example.myshop.component.notification.NotificationScreen
import com.example.myshop.component.profile.ProfileScreen
import com.example.myshop.ui.theme.Inactive
import com.example.myshop.ui.theme.MyShopTheme
import com.example.myshop.ui.theme.Primary
import com.example.myshop.util.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyShopContent(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    MyShopTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                BottomNavigationBar(navController,3)
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(route = Screen.Home.route) {
                    HomeScreen(navController)
                }

                composable(route = Screen.Notification.route) {
                    NotificationScreen(navController)
                }

                composable(route = Screen.Profile.route) {
                    ProfileScreen(navController)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController,
                        numberOfNotifications: Int
) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val destination = backStackEntry?.destination?.route

    val items = listOf(BottomNavItem.Home, BottomNavItem.Notify, BottomNavItem.Profile)
    val showBottomBar = items.any { item ->
        item.route == destination
    }

    if (showBottomBar) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.route == destination
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    if (destination != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(Screen.Home.route)
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box {
                            Icon(
                                painter = painterResource(id = if (isSelected) item.selected else item.unselected),
                                contentDescription = "Bottom item icon",
                                modifier = Modifier
                                    .padding(
                                        top = if(item == BottomNavItem.Notify) 5.dp else 0.dp,
                                        bottom = if(item == BottomNavItem.Notify) 5.dp else 0.dp,
                                        end = if(item == BottomNavItem.Notify) 5.dp else 0.dp,
                                    )
                                    .size(35.dp),
                                tint = if (isSelected) Primary else Inactive
                            )
                            if(item == BottomNavItem.Notify && numberOfNotifications > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = numberOfNotifications.toString(),
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.background(Primary, RoundedCornerShape(100))) {
                                Text(
                                    text = item.label,
                                    color = MaterialTheme.colorScheme.background,
                                    modifier = Modifier.padding(horizontal = 5.dp),
                                    maxLines = 1,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selected: Int,
    val unselected: Int
) {
    data object Home : BottomNavItem(
        Screen.Home.route,
        "Home",
        R.drawable.baseline_add_shopping_cart_24,
        R.drawable.baseline_add_shopping_cart_24
    )

    data object Notify : BottomNavItem(
        Screen.Notification.route,
        "Notifications",
        R.drawable.baseline_add_shopping_cart_24,
        R.drawable.baseline_add_shopping_cart_24
    )

    data object Profile : BottomNavItem(
        Screen.Profile.route,
        "Profile",
        R.drawable.baseline_add_shopping_cart_24,
        R.drawable.baseline_add_shopping_cart_24
    )
}