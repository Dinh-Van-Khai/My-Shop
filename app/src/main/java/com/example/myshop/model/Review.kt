package com.example.myshop.model

import java.util.Date

data class Review(
    var user: User = User(),
    var time: Date = Date(),
    var rate: Int = 0,
    var comment: String = "",
    var images: List<String> = emptyList()
)
