package com.vbshkn.ikollect

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class IKollectApplication : Application(), SingletonImageLoader.Factory {
    @Inject lateinit var loader: ImageLoader
    override fun newImageLoader(context: PlatformContext): ImageLoader = loader
}