package com.vbshkn.ikollect.data.mapper

import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.TagEntity
import com.vbshkn.ikollect.data.local.model.pojo.AlbumFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.AlbumWithArtists
import com.vbshkn.ikollect.data.local.model.pojo.ArtistFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.PhotocardWithArtists
import com.vbshkn.ikollect.data.remote.dao.ArtistDetailsResponse
import com.vbshkn.ikollect.data.remote.dao.FormatDao
import com.vbshkn.ikollect.data.remote.dao.FullReleaseData
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import com.vbshkn.ikollect.domain.model.list.AlbumListItem
import com.vbshkn.ikollect.domain.model.candidate.ArtistCandidate
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.model.profile.ArtistProfileData
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.domain.model.candidate.VersionCandidate
import com.vbshkn.ikollect.domain.model.profile.AlbumProfileData
import com.vbshkn.ikollect.util.ArtistNameHelper
import com.vbshkn.ikollect.util.UiText

object DataMappers {
    fun ArtistDetailsResponse.toCandidate(): ArtistCandidate {
        return ArtistCandidate(
            artistId = this.id,
            name = ArtistNameHelper.pickBestNameOption(
                apiName = this.name,
                nameVariations = this.nameVariations,
                isGroupName = !this.members.isNullOrEmpty()
            ),
            isGroup = !this.members.isNullOrEmpty(),
            isFavorite = false,
            profileImage = this.images.first().uri,
            memberIds = this.members?.map { it.id } ?: emptyList()
        )
    }

    fun ArtistDetailsResponse.toEntity(): ArtistEntity {
        return ArtistEntity(
            artistId = this.id,
            name = ArtistNameHelper.pickBestNameOption(
                apiName = this.name,
                nameVariations = this.nameVariations,
                isGroupName = !this.members.isNullOrEmpty()
            ),
            isGroup = !this.members.isNullOrEmpty(),
            isFavorite = false,
            imageUrl = if (this.images.isEmpty()) null
            else this.images.first().uri
        )
    }

    fun FullReleaseData.toCandidate(): AlbumCandidate {
        return AlbumCandidate(
            discogsAlbumId = this.searchResult.id,
            masterId = this.searchResult.masterId,
            barcodeNumber = this.barcode,
            name = this.releaseDetailsResponse.title,
            artistCandidates = this.artistDetailsResponses.map { it.toCandidate() },
            versionCandidates = mapVersionCandidates(this.availableVersions),
            releaseDate = this.searchResult.year,
            isFavorite = false,
            userNote = ""
        )
    }

    private fun mapVersionCandidates(versions: List<Pair<String?, List<FormatDao>?>>): List<VersionCandidate> {
        return versions.flatMap { (cover, formats) ->
            formats?.map { dao ->
                val versionName = dao.text ?: "Main"
                val versionType = dao.name?.let { "($it)" } ?: ""
                VersionCandidate(
                    name = "$versionName $versionType",
                    coverImage = cover
                )
            } ?: emptyList()
        }
    }

    fun AlbumWithArtists.toDetails(): AlbumDetails {
        return AlbumDetails(
            albumId = this.album.albumId,
            masterId = this.album.masterId,
            barcodeNumber = this.album.barcodeNumber,
            komcaNumber = this.album.komcaNumber,
            name = this.album.name,
            artists = this.artists.map { it.toListItem() },
            version = this.album.version ?: "No data",
            releaseDate = this.album.releaseDate ?: "No data",
            isFavorite = this.album.isFavorite,
            coverImage = this.album.imageUrl,
            userNote = this.album.userNote ?: "",
            savingTimestamp = this.album.timestamp
        )
    }

    fun AlbumEntity.toListItem(): AlbumListItem {
        return AlbumListItem(
            albumId = this.albumId,
            komcaNumber = this.komcaNumber,
            name = this.name,
            version = this.version ?: "",
            isFavorite = this.isFavorite,
            imageUrl = this.imageUrl ?: "",
            timestamp = this.timestamp
        )
    }

    fun ArtistCandidate.toEntity(): ArtistEntity {
        return ArtistEntity(
            artistId = this.artistId,
            name = this.name,
            isGroup = this.isGroup,
            isFavorite = this.isFavorite,
            imageUrl = this.profileImage
        )
    }

    fun PhotocardWithArtists.toListItem(): PhotocardListItem {
        return PhotocardListItem(
            photocardId = this.photocard.photocardId,
            owner = this.owner.toListItem(),
            displayName = this.photocard.displayName,
            tags = this.tags.map { it.toDomain() },
            imageUrl = this.photocard.imageUrl
        )
    }

    fun ArtistEntity.toListItem(): ArtistListItem {
        return ArtistListItem(
            artistId = this.artistId,
            name = this.name,
            isGroup = this.isGroup,
            isFavorite = this.isFavorite,
            profileImage = this.imageUrl
        )
    }

    fun ArtistFullDetail.toProfile(): ArtistProfileData {
        return if (this.artist.isGroup) {
            ArtistProfileData.GroupProfile(
                artist = this.artist.toListItem(),
                albums = this.albums.map { it.toDetails() },
                photocards = this.photocardsOwned.map { it.toListItem() },
                members = this.members.map { it.toListItem() }
            )
        } else {
            ArtistProfileData.SoloistProfile(
                artist = this.artist.toListItem(),
                albums = this.albums.map { it.toDetails() },
                photocards = this.photocardsDepicted.map { it.toListItem() },
                groups = this.groups.map { it.toListItem() }
            )
        }
    }
    
    fun AlbumFullDetail.toProfile(): AlbumProfileData {
        return AlbumProfileData(
            album = this.album.toDetails(),
            photocards = this.photocards.map { it.toListItem() }
        )
    }

    fun TagEntity.toDomain(): TagItem {
        return TagItem(
            id = this.tagId,
            isSystem = this.isSystemTag,
            name = if (this.isSystemTag) {
                val resId = SystemTag.getResId(this.tagName)
                if (resId != null) UiText.StringResource(resId)
                else UiText.DynamicString(this.tagName)
            } else UiText.DynamicString(this.tagName),
            color = this.tagColor
        )
    }
}