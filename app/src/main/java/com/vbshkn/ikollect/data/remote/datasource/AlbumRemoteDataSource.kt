package com.vbshkn.ikollect.data.remote.datasource

import com.vbshkn.ikollect.domain.AppError
import com.vbshkn.ikollect.data.remote.NetworkResult
import com.vbshkn.ikollect.data.remote.api.DiscogsApi
import com.vbshkn.ikollect.data.remote.dao.ArtistDetailsResponse
import com.vbshkn.ikollect.data.remote.dao.FullReleaseData
import com.vbshkn.ikollect.util.safeApiCall
import javax.inject.Inject

class AlbumRemoteDataSource @Inject constructor(
    private val api: DiscogsApi
) {
    suspend fun getFullReleaseData(barcode: String): NetworkResult<FullReleaseData> {
        // 1. Запрос основных сведений о релизе (поиск по коду)
        val searchResponse = safeApiCall { api.searchByBarcode(barcode) }
        if (searchResponse !is NetworkResult.Success) {
            return searchResponse as NetworkResult.Error
        }
        // собираем даннные обо всех версиях альбома в один список
        val versions = searchResponse.data.results.map { result ->
            if (result.coverImage.startsWith("https://i.discogs.com/")) {
                result.coverImage to result.formats
            }
            else { null to result.formats }
        }
            // избавляемся от дублей
            .sortedByDescending { it.first != null }
            .distinctBy { it.second }

        val release = searchResponse.data.results
            .firstOrNull()
            ?: return NetworkResult.Error(AppError.ReleaseNotFound)

        // 2. Запрос деталей релиза
        val detailsResponse = safeApiCall { api.getReleaseDetails(release.id) }
        if (detailsResponse !is NetworkResult.Success) {
            return detailsResponse as NetworkResult.Error
        }
        val details = detailsResponse.data

        // 3. Запрос деталей об исполнителях
        val artistsResponses = details.artists.map { artistDao ->
            safeApiCall { api.getArtistDetails(artistDao.id) }
        }
        val artists = artistsResponses
            .filterIsInstance<NetworkResult.Success<ArtistDetailsResponse>>()
            .map { it.data }

        return NetworkResult.Success(
            FullReleaseData(
                barcode = barcode,
                searchResult = release,
                releaseDetailsResponse = details,
                artistDetailsResponses = artists,
                availableVersions = versions
            )
        )
    }
}