package com.example.myshop.module

import com.example.myshop.util.FirebaseMessageConstant.BASE_URL
import com.example.myshop.util.NotificationAPI
import com.example.myshop.util.NotificationRepository
import com.example.myshop.util.NotificationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object APIModule {

    @Provides
    @ViewModelScoped
    fun provideNotificationAPI(): NotificationAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NotificationAPI::class.java)

    @Provides
    @ViewModelScoped
    fun provideRepository(notificationAPI: NotificationAPI) : NotificationRepository {
        return NotificationRepositoryImpl(notificationAPI)
    }

}