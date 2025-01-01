package com.example.myshop.util

import android.content.ContentResolver
import android.net.Uri

fun getFileExtension(uri: Uri, contentResolver: ContentResolver): String? {
    return contentResolver.getType(uri)?.substringAfterLast("/")
}