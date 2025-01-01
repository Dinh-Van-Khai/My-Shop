package com.example.myshop.util

import com.example.myshop.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

interface NotificationRepository {

    suspend fun postNotification(notification: PushNotification) : Response<ResponseBody>

}