package com.vbshkn.ikollect.di

import android.content.Context
import com.vbshkn.ikollect.data.local.datastore.LocalSettingsStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides @Singleton
    fun provideLocalSettingsStorage(
        @ApplicationContext context: Context
    ) = LocalSettingsStorage(context)
}