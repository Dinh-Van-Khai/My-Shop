package com.example.myshop.model

import java.util.Date

data class Message(
    var mid: String = "",
    var from: User = User(),
    var to: User = User(),
    var groupId: String = "",
    var text: String = "",
    var images: List<String> = emptyList(),
    var date: Date? = null,
    var read: Boolean = false
)
