package com.vbshkn.ikollect.di

import dagger.Binds
import com.vbshkn.ikollect.data.repository.AlbumRepositoryImpl
import com.vbshkn.ikollect.data.repository.ArtistRepositoryImpl
import com.vbshkn.ikollect.data.repository.AuthRepositoryImpl
import com.vbshkn.ikollect.data.repository.ImageRepositoryImpl
import com.vbshkn.ikollect.data.repository.PhotocardRepositoryImpl
import com.vbshkn.ikollect.data.repository.SettingsRepositoryImpl
import com.vbshkn.ikollect.data.repository.TagRepositoryImpl
import com.vbshkn.ikollect.domain.repository.AlbumRepository
import com.vbshkn.ikollect.domain.repository.ArtistRepository
import com.vbshkn.ikollect.domain.repository.AuthRepository
import com.vbshkn.ikollect.domain.repository.ImageRepository
import com.vbshkn.ikollect.domain.repository.PhotocardRepository
import com.vbshkn.ikollect.domain.repository.SettingsRepository
import com.vbshkn.ikollect.domain.repository.TagRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAlbumRepository(
        albumRepositoryImpl: AlbumRepositoryImpl
    ): AlbumRepository

    @Binds
    abstract fun bindArtistRepository(
        artistRepositoryImpl: ArtistRepositoryImpl
    ): ArtistRepository

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository

    @Binds
    abstract fun bindPhotocardRepository(
        photocardRepositoryImpl: PhotocardRepositoryImpl
    ): PhotocardRepository

    @Binds
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    abstract fun bindTagRepository(
        tagRepositoryImpl: TagRepositoryImpl
    ): TagRepository
}