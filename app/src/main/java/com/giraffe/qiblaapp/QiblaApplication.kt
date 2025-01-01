package com.giraffe.qiblaapp

import android.app.Application
import com.giraffe.qiblaapp.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class QiblaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@QiblaApplication)
            androidLogger()
            modules(appModule)
        }
    }
}