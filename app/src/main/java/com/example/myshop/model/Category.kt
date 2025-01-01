package com.example.myshop.model

data class Category(
    var name: String = "",
    var child: List<String> = emptyList()
)