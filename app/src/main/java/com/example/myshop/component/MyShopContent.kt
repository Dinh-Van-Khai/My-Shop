package com.example.myshop.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myshop.R
import com.example.myshop.component.cart.CartScreen
import com.example.myshop.component.change_password.ChangePasswordScreen
import com.example.myshop.component.chats.ChatsScreen
import com.example.myshop.component.check_out.CheckOutScreen
import com.example.myshop.component.edit_product.EditProductScreen
import com.example.myshop.component.home.HomeScreen
import com.example.myshop.component.message.MessageScreen
import com.example.myshop.component.my_likes.MyLikesScreen
import com.example.myshop.component.my_purchase.MyPurchaseScreen
import com.example.myshop.component.my_shop.MyShopScreen
import com.example.myshop.component.my_shop_purchase.MyShopPurchaseScreen
import com.example.myshop.component.notification.NotificationScreen
import com.example.myshop.component.product.ProductScreen
import com.example.myshop.component.profile.ProfileScreen
import com.example.myshop.component.review.ReviewScreen
import com.example.myshop.component.shop.ShopScreen
import com.example.myshop.component.sign_in.SignInScreen
import com.example.myshop.component.sign_up.SignUpScreen
import com.example.myshop.component.user.UserScreen
import com.example.myshop.model.Order
import com.example.myshop.model.Product
import com.example.myshop.ui.theme.Inactive
import com.example.myshop.ui.theme.MyShopTheme
import com.example.myshop.ui.theme.Primary
import com.example.myshop.util.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class, ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyShopContent(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: MyShopViewModel
) {
    val numberOfNotifications = viewModel.numberOfNotifications.collectAsStateWithLifecycle().value

    MyShopTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                BottomNavigationBar(navController,numberOfNotifications)
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
                composable(route = Screen.User.route) {
                    UserScreen(navController)
                }

                composable(route = Screen.ChangePassword.route) {
                    ChangePasswordScreen(navController, snackbarHostState)
                }

                composable(route = Screen.MyShop.route) {
                    MyShopScreen(navController, snackbarHostState)
                }

                composable(route = Screen.MyShopPurchases.route) {
                    MyShopPurchaseScreen(navController)
                }

                composable(route = Screen.MyLikes.route) {
                    MyLikesScreen(navController)
                }

                composable(
                    route = Screen.EditProduct.route + "?product={product}&label={label}&button={button}",
                    arguments = listOf(
                        navArgument("product") { type = NavType.StringType },
                        navArgument("label") { type = NavType.StringType },
                        navArgument("button") { type = NavType.StringType }
                    )
                ) {
                    val productJson = it.arguments?.getString("product") ?: ""
                    val product = try {
                        Gson().fromJson(productJson, Product::class.java) ?: Product()
                    } catch (e: Exception) {
                        Product()
                    }
                    val label = it.arguments?.getString("label") ?: "Add Product"
                    val button = it.arguments?.getString("button") ?: "Publish"
                    EditProductScreen(product, label, button, navController, snackbarHostState)
                }

                composable(
                    route = Screen.Product.route + "?pid={pid}",
                    arguments = listOf(navArgument("pid") { type = NavType.StringType })
                ) {
                    val pid = it.arguments?.getString("pid") ?: ""
                    ProductScreen(pid, snackbarHostState, navController)
                }

                composable(
                    route = Screen.Shop.route + "?uid={uid}",
                    arguments = listOf(
                        navArgument("uid") { type = NavType.StringType }
                    )
                ) {
                    val uid = it.arguments?.getString("uid") ?: ""
                    ShopScreen(uid, navController)
                }

                composable(route = Screen.Cart.route) {
                    CartScreen(navController)
                }

                composable(route = Screen.Chats.route) {
                    ChatsScreen(navController)
                }

                composable(
                    route = Screen.Message.route + "?othersId={othersId}",
                    arguments = listOf(
                        navArgument("othersId") { type = NavType.StringType }
                    )
                ) {
                    val othersId = it.arguments?.getString("othersId") ?: ""
                    MessageScreen(othersId, navController)
                }

                composable(
                    route = Screen.CheckOut.route + "?orders={orders}",
                    arguments = listOf(navArgument("orders") { type = NavType.StringType })
                ) {
                    val ordersString = it.arguments?.getString("orders")
                    val orders = try {
                        val listType = object : TypeToken<List<Order>>() {}.type
                        Gson().fromJson(ordersString, listType)
                    } catch (e: Exception) {
                        emptyList<Order>()
                    }
                    CheckOutScreen(orders, navController, snackbarHostState)
                }

                composable(route = Screen.MyPurchase.route) {
                    MyPurchaseScreen(navController)
                }

                composable(
                    route = Screen.Review.route + "?order={order}",
                    arguments = listOf(navArgument("order") { type = NavType.StringType })
                ) {
                    val orderJson = it.arguments?.getString("order") ?: ""
                    val order = try {
                        Gson().fromJson(orderJson, Order::class.java) ?: Order()
                    } catch (e: Exception) {
                        Order()
                    }
                    ReviewScreen(order, navController, snackbarHostState)
                }

                //Authentication
                composable(route = Screen.LogIn.route) {
                    SignInScreen(navController, snackbarHostState)
                }

                composable(route = Screen.SignUp.route) {
                    SignUpScreen(navController, snackbarHostState)
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
        R.drawable.ic_home_filled,
        R.drawable.ic_home_outline
    )

    data object Notify : BottomNavItem(
        Screen.Notification.route,
        "Notifications",
        R.drawable.ic_notification_filled,
        R.drawable.ic_notification_outline
    )

    data object Profile : BottomNavItem(
        Screen.Profile.route,
        "Profile",
        R.drawable.ic_profile_filled,
        R.drawable.ic_profile_outline
    )
}