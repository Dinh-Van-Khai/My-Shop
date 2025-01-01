package com.example.myshop.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.myshop.R
import com.example.myshop.MainActivity
import kotlinx.coroutines.FlowPreview
import kotlin.random.Random

@FlowPreview
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
class ShoppingMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val shoppingSF = getSharedPreferences("shopping_sf", Context.MODE_PRIVATE)
        shoppingSF.edit {
            putString("fcm_token", token)
            apply()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("route", message.data["route"])
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationId = Random.nextInt()
        val notification = NotificationCompat
            .Builder(this, getString(R.string.notification_chanel_id))
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setContentIntent(pendingIntent)
            .setColor(getColor(R.color.primary))
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }
}