package com.ebf.instant.di

import com.ebf.instant.local.AppDatabase
import com.ebf.instant.remote.FirestorePostDataSource
import com.ebf.instant.remote.PostDataSource
import com.ebf.instant.remote.StorageDataSource
import com.ebf.instant.repo.PostRepository
import com.ebf.instant.ui.camera.CameraScreenViewModel
import com.ebf.instant.ui.post.FeedScreenViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Firebase.firestore }
    single { Firebase.auth }
    single { Firebase.storage }
    single<PostDataSource> { FirestorePostDataSource(get()) }
    single { StorageDataSource(get(), get()) }
    single { PostRepository(get(), get(), get(), get()) }
    single { AppDatabase.init(androidContext()) }
    factory { get<AppDatabase>().postDao() }
    viewModel { FeedScreenViewModel(get()) }
    viewModel { CameraScreenViewModel(get()) }
}