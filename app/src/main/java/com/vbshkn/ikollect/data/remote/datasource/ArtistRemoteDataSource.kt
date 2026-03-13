package com.vbshkn.ikollect.data.remote.datasource

import com.vbshkn.ikollect.data.remote.api.DiscogsApi
import com.vbshkn.ikollect.data.remote.dao.ArtistDetailsResponse
import retrofit2.Response
import javax.inject.Inject

class ArtistRemoteDataSource @Inject constructor(
    private val api: DiscogsApi
) {
    suspend fun getArtistDetails(artistId: Int): Response<ArtistDetailsResponse> {
        return api.getArtistDetails(artistId)
    }
}