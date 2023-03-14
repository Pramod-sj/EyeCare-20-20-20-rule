package com.pramod.eyecare.framework

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore("eye_care_data")

@HiltAndroidApp
class EyeCareApp : Application() {


    companion object {

    }

    override fun onCreate() {
        super.onCreate()
        plant(object : Timber.DebugTree() {
            /**
             * Override [log] to modify the tag and add a "global tag" prefix to it. You can rename the String "global_tag_" as you see fit.
             */
            override fun log(
                priority: Int, tag: String?, message: String, t: Throwable?,
            ) {
                super.log(priority, "global_tag_$tag", message, t)
            }
        })
    }
}