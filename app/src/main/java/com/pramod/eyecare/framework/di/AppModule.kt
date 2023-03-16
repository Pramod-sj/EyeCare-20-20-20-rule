package com.pramod.eyecare.framework.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Context.*
import android.os.Vibrator
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.pramod.eyecare.framework.appDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(ALARM_SERVICE) as AlarmManager
    }

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.appDataStore
    }

    @Provides
    fun provideGson(): Gson = Gson()


    @Provides
    fun provideVibrator(@ApplicationContext context: Context): Vibrator {
        return context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    @Provides
    fun provideLocalBroadcastManager(@ApplicationContext context: Context): LocalBroadcastManager {
        return LocalBroadcastManager.getInstance(context)
    }

    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return FirebaseRemoteConfig.getInstance()
    }
}
