package com.ebf.instant.di

import com.ebf.instant.local.AppDatabase
import com.ebf.instant.remote.FirestorePostDataSource
import com.ebf.instant.remote.PostDataSource
import com.ebf.instant.repo.PostRepository
import com.ebf.instant.ui.post.FeedScreenViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Firebase.firestore }
    single<PostDataSource> { FirestorePostDataSource(get()) }
    single { PostRepository(get(), get()) }
    single { AppDatabase.init(androidContext()) }
    factory { get<AppDatabase>().postDao() }
    viewModel { FeedScreenViewModel(get()) }
}