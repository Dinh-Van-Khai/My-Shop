package com.example.myshop.model

data class Like(
    var lid: String = "",
    var liker: User = User(),
    var user: User = User()
)
