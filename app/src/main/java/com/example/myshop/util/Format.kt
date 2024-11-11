package com.example.myshop.util

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat

val decimalFormat = DecimalFormat("###,###,###,###")

@SuppressLint("SimpleDateFormat")
val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")