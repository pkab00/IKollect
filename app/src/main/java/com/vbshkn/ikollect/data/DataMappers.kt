package com.vbshkn.ikollect.data

import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.pojo.AlbumWithArtists
import com.vbshkn.ikollect.data.remote.dao.ArtistDetailsResponse
import com.vbshkn.ikollect.data.remote.dao.FormatDao
import com.vbshkn.ikollect.data.remote.dao.FullReleaseData
import com.vbshkn.ikollect.domain.model.Album
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.domain.model.Artist
import com.vbshkn.ikollect.domain.model.ArtistCandidate
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
            savingDate = this.album.timestamp.toDateString()
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
}