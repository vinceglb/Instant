package com.ebf.instant

import android.app.Application
import com.ebf.instant.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }

    /**
     * TODO Add Crashlytics in RELEASE mode
     */
    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(Timber.DebugTree())
            // ("Timber.plant(CrashlyticsTree())")
        }
    }

}