package com.vbshkn.ikollect.data.mapper

import com.vbshkn.ikollect.data.local.datastore.LocalSettings
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
import com.vbshkn.ikollect.data.remote.backend.model.UserSettingsBackend
import com.vbshkn.ikollect.util.nowTimestamp
import kotlin.time.Instant

object BackendMappers {
    const val NULL_UID = "00000000-0000-0000-0000-000000000000"

    // BACKEND ||==================================>>> ENTITY

    fun AlbumArtistCrossRefBackend.toEntity(): AlbumArtistCrossRef {
        return AlbumArtistCrossRef(
            albumId = this.albumId,
            artistId = this.artistId,
            createdAt = this.createdAt.toTimeMillis(),
            updatedAt = this.updatedAt.toTimeMillis(),
            isDeleted = this.isDeleted
        )
    }

    fun PhotocardArtistCrossRefBackend.toEntity(): PhotocardArtistCrossRef {
        return PhotocardArtistCrossRef(
            photocardId = this.photocardId,
            artistId = this.artistId,
            createdAt = this.createdAt.toTimeMillis(),
            updatedAt = this.updatedAt.toTimeMillis(),
            isDeleted = this.isDeleted
        )
    }

    fun PhotocardTagCrossRefBackend.toEntity(): PhotocardTagCrossRef {
        return PhotocardTagCrossRef(
            photocardId = this.photocardId,
            tagId = this.tagId,
            createdAt = this.createdAt.toTimeMillis(),
            updatedAt = this.updatedAt.toTimeMillis(),
            isDeleted = this.isDeleted
        )
    }

    fun GlobalArtistBackend.toEntity(settings: UserArtistSettingsBackend): ArtistEntity {
        return ArtistEntity(
            artistId = this.artistId,
            name = this.name,
            isGroup = this.isGroup,
            isFavorite = settings.isFavorite,
            imageUrl = this.imageUrl,
            createdAt = this.createdAt.toTimeMillis(),
            updatedAt = this.updatedAt.toTimeMillis()
        )
    }

    fun GlobalArtistHierarchyBackend.toEntity(): ArtistArtistCrossRef {
        return ArtistArtistCrossRef(
            groupId = this.groupId,
            memberId = this.memberId,
            createdAt = this.createdAt.toTimeMillis(),
            updatedAt = this.updatedAt.toTimeMillis()
        )
    }

    fun TagBackend.toEntity(): TagEntity {
        return TagEntity(
            tagId = this.tagId ?: 0L,
            isSystemTag = this.isSystemTag,
            tagName = this.tagName,
            tagColor = this.tagColor,
            createdAt = this.createdAt.toTimeMillis(),
            updatedAt = this.updatedAt.toTimeMillis(),
            isDeleted = this.isDeleted
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
            createdAt = this.createdAt.toTimeMillis(),
            updatedAt = this.updatedAt.toTimeMillis(),
            isDeleted = this.isDeleted
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
            createdAt = this.createdAt.toTimeMillis(),
            updatedAt = this.updatedAt.toTimeMillis(),
            isDeleted = this.isDeleted
        )
    }

    // BACKEND <<<==================================|| ENTITY

    fun AlbumArtistCrossRef.toBackend(userId: String): AlbumArtistCrossRefBackend {
        return AlbumArtistCrossRefBackend(
            albumId = this.albumId,
            artistId = this.artistId,
            userId = userId,
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz(),
            isDeleted = this.isDeleted
        )
    }

    fun PhotocardArtistCrossRef.toBackend(userId: String): PhotocardArtistCrossRefBackend {
        return PhotocardArtistCrossRefBackend(
            photocardId = this.photocardId,
            artistId = this.artistId,
            userId = userId,
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz(),
            isDeleted = this.isDeleted
        )
    }

    fun PhotocardTagCrossRef.toBackend(userId: String): PhotocardTagCrossRefBackend {
        return PhotocardTagCrossRefBackend(
            photocardId = this.photocardId,
            tagId = this.tagId,
            ownerId = userId,
            creatorId = if (this.tagId < 0) NULL_UID else userId,
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz(),
            isDeleted = this.isDeleted
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
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz(),
            isDeleted = this.isDeleted
        )
    }

    fun ArtistArtistCrossRef.toBackend(): GlobalArtistHierarchyBackend {
        return GlobalArtistHierarchyBackend(
            groupId = this.groupId,
            memberId = this.memberId,
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz()
        )
    }

    fun ArtistEntity.toBackend(): GlobalArtistBackend {
        return GlobalArtistBackend(
            artistId = this.artistId,
            name = this.name,
            isGroup = this.isGroup,
            imageUrl = this.imageUrl,
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz(),
        )
    }

    fun ArtistEntity.toBackend(userId: String): UserArtistSettingsBackend {
        return UserArtistSettingsBackend(
            userId = userId,
            artistId = this.artistId,
            isFavorite = this.isFavorite,
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz(),
            isDeleted = this.isDeleted
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
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz(),
            isDeleted = this.isDeleted
        )
    }

    fun TagEntity.toBackend(userId: String): TagBackend {
        return TagBackend(
            tagId = this.tagId,
            userId = userId,
            isSystemTag = false,
            tagName = this.tagName,
            tagColor = this.tagColor,
            createdAt = this.createdAt.toTimestamptz(),
            updatedAt = this.updatedAt.toTimestamptz(),
            isDeleted = this.isDeleted
        )
    }

    fun LocalSettings.toBackend(userId: String): UserSettingsBackend {
        return UserSettingsBackend(
            id = userId,
            settings = this,
            updatedAt = nowTimestamp()
        )
    }
}

fun String?.toTimeMillis(): Long {
    return this?.let { Instant.parse(it).toEpochMilliseconds() } ?: 0L
}

fun Long.toTimestamptz(): String {
    return Instant.fromEpochMilliseconds(this).toString()
}