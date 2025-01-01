package com.example.myshop.util

import com.example.myshop.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

class NotificationRepositoryImpl(
    private val notificationAPI: NotificationAPI
) : NotificationRepository {

    override suspend fun postNotification(notification: PushNotification): Response<ResponseBody> {
        return notificationAPI.postNotification(notification)
    }

}