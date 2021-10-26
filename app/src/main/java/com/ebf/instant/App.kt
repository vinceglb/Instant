package com.ebf.instant

import android.app.Application
import com.ebf.instant.di.appModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.android.get
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
        initFirebaseEmulators()
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

    private fun initFirebaseEmulators() {
        // Setup Firebase emulators during development
        if (BuildConfig.DEBUG) {
            // Authentication
            val auth = get<FirebaseAuth>()
            auth.useEmulator("10.0.2.2", 9099)

            // Storage
            val storage = get<FirebaseStorage>()
            storage.useEmulator("10.0.2.2", 9199)

            // Firestore
            val firestore = get<FirebaseFirestore>()
            val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
            firestore.useEmulator("10.0.2.2", 8080)
            firestore.firestoreSettings = settings
        }
    }

}