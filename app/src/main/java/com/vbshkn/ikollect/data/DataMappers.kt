package com.vbshkn.ikollect.data

import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.pojo.AlbumWithArtists
import com.vbshkn.ikollect.data.local.model.pojo.ArtistFullDetail
import com.vbshkn.ikollect.data.local.model.pojo.ArtistMinimalDetail
import com.vbshkn.ikollect.data.local.model.pojo.PhotocardWithArtists
import com.vbshkn.ikollect.data.remote.dao.ArtistDetailsResponse
import com.vbshkn.ikollect.data.remote.dao.FormatDao
import com.vbshkn.ikollect.data.remote.dao.FullReleaseData
import com.vbshkn.ikollect.domain.model.Album
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.domain.model.Artist
import com.vbshkn.ikollect.domain.model.ArtistCandidate
import com.vbshkn.ikollect.domain.model.ArtistOverview
import com.vbshkn.ikollect.domain.model.ArtistProfileData
import com.vbshkn.ikollect.domain.model.Photocard
import com.vbshkn.ikollect.domain.model.VersionCandidate
import com.vbshkn.ikollect.util.ArtistNameHelper
import com.vbshkn.ikollect.util.TimeUtil.toDateString

object DataMappers {
    fun ArtistDetailsResponse.toDomain(): ArtistCandidate {
        return ArtistCandidate(
            artistId = this.id,
            name = ArtistNameHelper.pickBestNameOption(this.name, this.nameVariations),
            isGroup = !this.members.isNullOrEmpty(),
            isFavorite = false,
            profileImage = this.images.first().uri,
            memberIds = this.members?.map { it.id } ?: emptyList()
        )
    }

    fun ArtistDetailsResponse.toEntity(): ArtistEntity {
        return ArtistEntity(
            artistId = this.id,
            name = ArtistNameHelper.pickBestNameOption(this.name, this.nameVariations),
            isGroup = !this.members.isNullOrEmpty(),
            isFavorite = false,
            imageUrl = if (this.images.isEmpty()) null
                       else this.images.first().uri
        )
    }

    fun FullReleaseData.toDomain(): AlbumCandidate {
        return AlbumCandidate(
            discogsAlbumId = this.searchResult.id,
            masterId = this.searchResult.masterId,
            barcodeNumber = this.barcode,
            name = this.releaseDetailsResponse.title,
            artistCandidates = this.artistDetailsResponses.map { it.toDomain() },
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

    fun AlbumWithArtists.toDomain(): Album {
        return Album(
            albumId = this.album.albumId,
            masterId = this.album.masterId,
            barcodeNumber = this.album.barcodeNumber,
            name = this.album.name,
            artists = this.artists.map { it.toDomain() },
            version = this.album.version ?: "No data",
            releaseDate = this.album.releaseDate ?: "No data",
            isFavorite = this.album.isFavorite,
            coverImage = this.album.imageUrl,
            userNote = this.album.userNote ?: "",
            savingTimestamp = this.album.timestamp
        )
    }

    fun ArtistEntity.toDomain(): Artist {
        return Artist(
            artistId = this.artistId,
            name = this.name,
            isGroup = this.isGroup,
            isFavorite = this.isFavorite,
            profileImage = this.imageUrl
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

    fun PhotocardWithArtists.toDomain(): Photocard {
        return Photocard(
            photocardId = this.photocard.photocardId,
            albumId = this.photocard.albumId,
            ownerId = this.photocard.ownerId,
            displayName = this.photocard.displayName,
            depictedArtists = this.artists.map { it.toDomain() },
            isPob = this.photocard.isPob,
            isFavorite = this.photocard.isFavorite,
            imageUrl = this.photocard.imageUrl,
            userNote = this.photocard.userNote
        )
    }

    fun ArtistMinimalDetail.toDomain(): ArtistOverview {
        return if (this.isGroup) {
            ArtistOverview(
                artistId = this.artistId,
                name = this.name,
                isGroup = true,
                isFavorite = this.isFavorite,
                imageUrl = this.imageUrl,
                albumsCount = this.albumsCount,
                photocardsCount = this.photocardsOwnedCount
            )
        }
        else {
            ArtistOverview(
                artistId = this.artistId,
                name = this.name,
                isGroup = false,
                isFavorite = this.isFavorite,
                imageUrl = this.imageUrl,
                albumsCount = this.albumsCount,
                photocardsCount = this.photocardsDepictedCount
            )
        }
    }

    fun ArtistFullDetail.toDomain(): ArtistProfileData {
        return if (this.artist.isGroup) {
            ArtistProfileData.GroupProfile(
                artist = this.artist.toDomain(),
                albums = this.albums.map { it.toDomain() },
                photocards = this.photocardsOwned.map { it.toDomain() },
                members = this.members.map { it.toDomain() }
            )
        }
        else {
            ArtistProfileData.SoloistProfile(
                artist = this.artist.toDomain(),
                albums = this.albums.map { it.toDomain() },
                photocards = this.photocardsDepicted.map { it.toDomain() },
                groups = this.groups.map { it.toDomain() }
            )
        }
    }
}