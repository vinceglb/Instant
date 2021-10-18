package com.ebf.instant.di

import com.ebf.instant.local.AppDatabase
import com.ebf.instant.local.DataStorePreferencesStorage
import com.ebf.instant.local.PreferencesStorage
import com.ebf.instant.local.dataStore
import com.ebf.instant.remote.*
import com.ebf.instant.repo.PostRepository
import com.ebf.instant.repo.UserRepository
import com.ebf.instant.ui.camera.CameraScreenViewModel
import com.ebf.instant.ui.login.LoginScreenViewModel
import com.ebf.instant.ui.post.FeedScreenViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase
    single { Firebase.firestore }
    single { Firebase.auth }
    single { Firebase.storage }

    // Data source
    single<PostDataSource> { FirestorePostDataSource(get()) }
    single<UserDataSource> { FirestoreUserDataSource(get()) }
    single { StorageDataSource(get(), get(), androidContext()) }

    // Repositories
    single { PostRepository(get(), get(), get(), get(), get()) }
    single { UserRepository(get()) }

    // Room db
    single { AppDatabase.init(androidContext()) }
    factory { get<AppDatabase>().postDao() }
    factory { get<AppDatabase>().userDao() }

    // Preferences DataStore
    factory { androidContext().dataStore }
    single<PreferencesStorage> { DataStorePreferencesStorage(get()) }

    // ViewModels
    viewModel { FeedScreenViewModel(get()) }
    viewModel { CameraScreenViewModel(get()) }
    viewModel { LoginScreenViewModel(get(), get(), get()) }
}