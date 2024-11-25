package com.example.myshop.model

import java.util.Date

data class Notification(
    var nid: String = "",
    var to: String = "",
    var image: String = "",
    var title: String = "",
    var message: String = "",
    var date: Date? = null,
    var seen: Boolean = false,
    var read: Boolean = false,
    var route: String = ""
)
