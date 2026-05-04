package com.vbshkn.ikollect

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.vbshkn.ikollect.data.background.BackgroundSyncWorker
import com.vbshkn.ikollect.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class IKollectApplication : Application(), SingletonImageLoader.Factory, Configuration.Provider {
    @Inject lateinit var loader: ImageLoader
    override fun newImageLoader(context: PlatformContext): ImageLoader = loader

    @Inject lateinit var workerFactory: HiltWorkerFactory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}