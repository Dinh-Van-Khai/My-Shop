package com.example.myshop.util

sealed class Screen(val route: String) {

    //Authentication
    object LogIn : Screen(route = "login_screen")
    object SignUp : Screen(route = "signup_screen")

    //Using
    object Home : Screen(route = "home_screen")
    object Notification : Screen(route = "notifications_screen")
    object Profile : Screen(route = "profile_screen")
    object User : Screen(route = "user_screen")
    object ChangePassword : Screen(route = "change_password_screen")
    object MyShop : Screen(route = "my_shop_screen")
    object MyShopPurchases : Screen(route = "my_shop_purchases_screen")
    object MyPurchase : Screen(route = "my_purchase_screen")
    object MyLikes : Screen(route = "my_likes_screen")
    object EditProduct : Screen(route = "edit_product_screen")
    object Product : Screen(route = "product_screen")
    object Shop : Screen(route = "shop_screen")
    object Cart : Screen(route = "cart_screen")
    object Chats : Screen(route = "chats_screen")
    object Message : Screen(route = "message_screen")
    object CheckOut : Screen(route = "check_out_screen")
    object Review : Screen(route = "review_screen")
}