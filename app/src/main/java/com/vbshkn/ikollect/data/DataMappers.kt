package com.vbshkn.ikollect.data

import com.vbshkn.ikollect.data.local.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.pojo.AlbumWithArtists
import com.vbshkn.ikollect.data.remote.dao.ArtistDetailsResponse
import com.vbshkn.ikollect.data.remote.dao.FormatDao
import com.vbshkn.ikollect.data.remote.dao.FullReleaseData
import com.vbshkn.ikollect.domain.model.Album
import com.vbshkn.ikollect.domain.model.AlbumCandidate
import com.vbshkn.ikollect.domain.model.Artist
import com.vbshkn.ikollect.domain.model.VersionCandidate

object DataMappers {
    fun ArtistDetailsResponse.toDomain(): Artist {
        return Artist(
            artistId = this.id,
            name = if (this.nameVariations.isNullOrEmpty()) this.name
                   else this.nameVariations.first(),
            isGroup = !this.members.isNullOrEmpty(),
            members = null,
            isFavorite = false,
            profileImage = null
        )
    }

    fun FullReleaseData.toDomain(): AlbumCandidate {
        return AlbumCandidate(
            albumId = this.searchResult.id,
            masterId = this.searchResult.masterId,
            barcodeNumber = this.barcode,
            name = this.releaseDetailsResponse.title,
            artists = this.artistDetailsResponses.map { it.toDomain() },
            versionCandidates = mapVersionCandidates(this.availableVersions),
            releaseDate = this.searchResult.year,
            isFavorite = false,
            userNote = ""
        )
    }

    private fun mapVersionCandidates(versions: List<Pair<String?, List<FormatDao>?>>): List<VersionCandidate> {
        return versions.flatMap { (cover, formats) ->
            formats?.map { dao ->
                val versionName = dao.text ?: "No data"
                val versionType = dao.name?.let { "($it)" } ?: ""
                VersionCandidate(
                    name = "$versionName $versionType",
                    coverImage = cover
                )
            } ?: emptyList()
        }
    }

    fun AlbumCandidate.toAlbum(
        selectedVersion: String,
        selectedCover: String
    ): Album {
        return Album(
            albumId = this.albumId,
            masterId = this.masterId,
            barcodeNumber = this.barcodeNumber,
            name = this.name,
            artists = this.artists,
            version = selectedVersion,
            releaseDate = this.releaseDate,
            isFavorite = this.isFavorite,
            coverImage = selectedCover,
            userNote = this.userNote
        )
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
            userNote = this.album.userNote ?: ""
        )
    }

    fun ArtistEntity.toDomain(): Artist {
        return Artist(
            artistId = this.artistId,
            name = this.name,
            isGroup = this.isGroup,
            members = null,
            isFavorite = this.isFavorite,
            profileImage = this.imageUrl
        )
    }
}