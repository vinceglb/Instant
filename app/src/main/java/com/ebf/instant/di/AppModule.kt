package com.ebf.instant.di

import com.ebf.instant.data.AuthRepository
import com.ebf.instant.data.comment.CommentRepository
import com.ebf.instant.data.comment.FunctionsCommentDataSource
import com.ebf.instant.data.db.AppDatabase
import com.ebf.instant.data.post.FirestorePostDataSource
import com.ebf.instant.data.post.FunctionsPostDataSource
import com.ebf.instant.data.post.PostRepository
import com.ebf.instant.data.post.StoragePostDataSource
import com.ebf.instant.data.prefs.DataStorePreferencesStorage
import com.ebf.instant.data.prefs.PreferencesStorage
import com.ebf.instant.data.prefs.dataStore
import com.ebf.instant.data.signin.datasources.AuthStateUserDataSource
import com.ebf.instant.data.signin.datasources.FirebaseAuthStateUserDataSource
import com.ebf.instant.data.signin.datasources.FirestoreRegisteredUserDataSource
import com.ebf.instant.data.signin.datasources.RegisteredUserDataSource
import com.ebf.instant.data.user.FirestoreUserDataSource
import com.ebf.instant.data.user.FunctionsUserDataSource
import com.ebf.instant.data.user.UserRepository
import com.ebf.instant.domain.auth.ObserveUserAuthStateUseCase
import com.ebf.instant.fcm.FcmTokenUpdater
import com.ebf.instant.ui.InstantAppViewModel
import com.ebf.instant.ui.camera.CameraScreenViewModel
import com.ebf.instant.ui.comment.CommentScreenViewModel
import com.ebf.instant.ui.create.CreateAccountViewModel
import com.ebf.instant.ui.feed.FeedViewModel
import com.ebf.instant.ui.login.LoginScreenViewModel
import com.ebf.instant.ui.signin.FirebaseSignInViewModelDelegate
import com.ebf.instant.ui.signin.SignInViewModelDelegate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase
    single { Firebase.firestore }
    single { Firebase.auth }
    single { Firebase.storage }
    single { Firebase.functions("europe-west1") }

    // Data sources
    single { FirestoreUserDataSource(get()) }
    single { FirestorePostDataSource(get()) }
    single { FunctionsPostDataSource(get()) }
    single { FunctionsUserDataSource(get()) }
    single { FunctionsCommentDataSource(get()) }
    single { StoragePostDataSource(get(), get()) }

    // Repositories
    single { UserRepository(get(), get()) }
    single { AuthRepository(get()) }
    single { PostRepository(get(), get(), get(), get(), get(), get(), get()) }
    single { CommentRepository(get(), get()) }

    // Room db
    single { AppDatabase.init(androidContext()) }
    factory { get<AppDatabase>().postDao() }
    factory { get<AppDatabase>().userDao() }
    factory { get<AppDatabase>().commentDao() }
    factory { get<AppDatabase>().likeDao() }

    // Preferences DataStore
    factory { androidContext().dataStore }
    single<PreferencesStorage> { DataStorePreferencesStorage(get()) }

    // Scope
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    // ViewModels
    viewModel { InstantAppViewModel(get()) }
    viewModel { CreateAccountViewModel(get(), get(), get()) }
    viewModel { LoginScreenViewModel(get()) }
    viewModel { FeedViewModel(get(), get(), get()) }
    viewModel { CameraScreenViewModel(get(), get()) }
    viewModel { params -> CommentScreenViewModel(get(), get(), get(), postId = params.get()) }

    factory<RegisteredUserDataSource> { FirestoreRegisteredUserDataSource(get()) }
    single<AuthStateUserDataSource> { FirebaseAuthStateUserDataSource(get(), get(), get()) }
    factory { FcmTokenUpdater(get(), get()) }

    single { ObserveUserAuthStateUseCase(get(), get(), get()) }
    single<SignInViewModelDelegate> { FirebaseSignInViewModelDelegate(get(), get()) }
}