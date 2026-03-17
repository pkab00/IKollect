package com.vbshkn.ikollect.di

import android.content.Context
import com.vbshkn.ikollect.data.service.BarcodeScannerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides @Singleton
    fun provideBarcodeScanner(
        @ApplicationContext context: Context
    ): BarcodeScannerService {
        return BarcodeScannerService(context)
    }
}