package com.vbshkn.ikollect.data.mapper

import androidx.compose.ui.graphics.Color
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import com.vbshkn.ikollect.data.local.model.entity.TagEntity
import com.vbshkn.ikollect.data.remote.backend.model.AlbumArtistCrossRefBackend
import com.vbshkn.ikollect.data.remote.backend.model.GlobalArtistBackend
import com.vbshkn.ikollect.data.remote.backend.model.GlobalArtistHierarchyBackend
import com.vbshkn.ikollect.data.remote.backend.model.PhotocardArtistCrossRefBackend
import com.vbshkn.ikollect.data.remote.backend.model.PhotocardTagCrossRefBackend
import com.vbshkn.ikollect.data.remote.backend.model.TagBackend
import com.vbshkn.ikollect.data.remote.backend.model.UserAlbumBackend
import com.vbshkn.ikollect.data.remote.backend.model.UserArtistSettingsBackend
import com.vbshkn.ikollect.data.remote.backend.model.UserPhotocardBackend
import kotlin.time.Instant

object BackendMappers {
    // BACKEND ||==================================>>> ENTITY

    fun AlbumArtistCrossRefBackend.toEntity(): AlbumArtistCrossRef {
        return AlbumArtistCrossRef(
            albumId = this.albumId,
            artistId = this.artistId
        )
    }

    fun PhotocardArtistCrossRefBackend.toEntity(): PhotocardArtistCrossRef {
        return PhotocardArtistCrossRef(
            photocardId = this.photocardId,
            artistId = this.artistId
        )
    }

    fun PhotocardTagCrossRefBackend.toEntity(): PhotocardTagCrossRef {
        return PhotocardTagCrossRef(
            photocardId = this.photocardId,
            tagId = this.tagId
        )
    }

    fun GlobalArtistBackend.toEntity(settings: UserArtistSettingsBackend): ArtistEntity {
        return ArtistEntity(
            artistId = this.artistId,
            name = this.name,
            isGroup = this.isGroup,
            isFavorite = settings.isFavorite,
            imageUrl = this.imageUrl
        )
    }

    fun GlobalArtistHierarchyBackend.toEntity(): ArtistArtistCrossRef {
        return ArtistArtistCrossRef(
            groupId = this.groupId,
            memberId = this.memberId
        )
    }

    fun TagBackend.toEntity(): TagEntity {
        return TagEntity(
            tagId = this.tagId ?: 0L,
            isSystemTag = this.isSystemTag,
            tagName = this.tagName,
            tagColor = Color(this.tagColor)
        )
    }

    fun UserAlbumBackend.toEntity(): AlbumEntity {
        return AlbumEntity(
            albumId = this.albumId ?: 0L,
            masterId = this.masterId ?: 0L,
            barcodeNumber = this.barcodeNumber ?: "",
            komcaNumber = this.komcaNumber,
            name = this.name,
            version = this.version,
            releaseDate = this.releaseDate,
            isFavorite = this.isFavorite,
            imageUrl = this.imageUrl,
            userNote = this.userNote,
            timestamp = this.createdAt?.let { Instant.parse(it).toEpochMilliseconds() } ?: 0L
        )
    }

    fun UserPhotocardBackend.toEntity(): PhotocardEntity {
        return PhotocardEntity(
            photocardId = this.photocardId ?: 0L,
            albumId = this.albumId,
            ownerId = this.ownerId,
            displayName = this.displayName,
            isFavorite = this.isFavorite,
            imageUrl = this.imageUrl,
            userNote = this.userNote,
            savingTimestamp = this.savingTimestamp?.let { Instant.parse(it).toEpochMilliseconds() } ?: 0L
        )
    }

    // BACKEND <<<==================================|| ENTITY

    fun AlbumArtistCrossRef.toBackend(userId: String): AlbumArtistCrossRefBackend {
        return AlbumArtistCrossRefBackend(
            albumId = this.albumId,
            artistId = this.artistId,
            userId = userId
        )
    }

    fun PhotocardArtistCrossRef.toBackend(userId: String): PhotocardArtistCrossRefBackend {
        return PhotocardArtistCrossRefBackend(
            photocardId = this.photocardId,
            artistId = this.artistId,
            userId = userId
        )
    }

    fun PhotocardTagCrossRef.toBackend(userId: String): PhotocardTagCrossRefBackend {
        return PhotocardTagCrossRefBackend(
            photocardId = this.photocardId,
            tagId = this.tagId,
            ownerId = userId
        )
    }

    fun AlbumEntity.toBackend(userId: String): UserAlbumBackend {
        return UserAlbumBackend(
            albumId = this.albumId,
            userId = userId,
            masterId = this.masterId,
            barcodeNumber = this.barcodeNumber,
            komcaNumber = this.komcaNumber,
            name = this.name,
            version = this.version,
            releaseDate = this.releaseDate,
            isFavorite = this.isFavorite,
            imageUrl = this.imageUrl,
            userNote = this.userNote,
            createdAt = Instant.fromEpochMilliseconds(this.timestamp).toString()
        )
    }

    fun ArtistArtistCrossRef.toBackend(): GlobalArtistHierarchyBackend {
        return GlobalArtistHierarchyBackend(
            groupId = this.groupId,
            memberId = this.memberId
        )
    }

    fun ArtistEntity.toBackend(): GlobalArtistBackend {
        return GlobalArtistBackend(
            artistId = this.artistId,
            name = this.name,
            isGroup = this.isGroup,
            imageUrl = this.imageUrl
        )
    }

    fun ArtistEntity.toBackend(userId: String): UserArtistSettingsBackend {
        return UserArtistSettingsBackend(
            userId = userId,
            artistId = this.artistId,
            isFavorite = this.isFavorite
        )
    }

    fun PhotocardEntity.toBackend(userId: String): UserPhotocardBackend {
        return UserPhotocardBackend(
            photocardId = this.photocardId,
            userId = userId,
            albumId = this.albumId,
            ownerId = this.ownerId,
            displayName = this.displayName,
            isFavorite = this.isFavorite,
            imageUrl = this.imageUrl,
            userNote = this.userNote,
            savingTimestamp = Instant.fromEpochMilliseconds(this.savingTimestamp).toString()
        )
    }

    fun TagEntity.toBackend(userId: String): TagBackend {
        return TagBackend(
            tagId = this.tagId,
            userId = userId,
            isSystemTag = false,
            tagName = this.tagName,
            tagColor = this.tagColor.value.toLong()
        )
    }
}