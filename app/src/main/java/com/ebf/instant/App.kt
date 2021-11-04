package com.ebf.instant

import android.app.Application
import com.ebf.instant.di.appModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.functions.FirebaseFunctions
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
            val ip = "192.168.1.98" // "10.0.2.2" // "192.168.1.98"

            // Authentication
            val auth = get<FirebaseAuth>()
            auth.useEmulator(ip, 9099)

            // Storage
            val storage = get<FirebaseStorage>()
            storage.useEmulator(ip, 9199)

            // Functions
            val functions = get<FirebaseFunctions>()
            functions.useEmulator(ip, 5001)

            // Firestore
            val firestore = get<FirebaseFirestore>()
            val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
            firestore.useEmulator(ip, 8080)
            firestore.firestoreSettings = settings
        }
    }

}