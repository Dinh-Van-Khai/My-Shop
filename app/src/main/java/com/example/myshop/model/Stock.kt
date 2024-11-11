package com.example.myshop.model

data class Stock(
    var variations: List<String> = emptyList(),
    var quantity: Long = 0
)
