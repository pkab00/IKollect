package com.vbshkn.ikollect.data.remote.api

import com.vbshkn.ikollect.data.remote.dao.ArtistDetailsResponse
import com.vbshkn.ikollect.data.remote.dao.ReleaseDetailsResponse
import com.vbshkn.ikollect.data.remote.dao.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApi {
    @GET("database/search")
    suspend fun searchByBarcode(
        @Query("barcode") barcode: String
    ): Response<SearchResponse>

    @GET("releases/{release_id}")
    suspend fun getReleaseDetails(
        @Path("release_id") releaseId: Long
    ): Response<ReleaseDetailsResponse>

    @GET("artists/{artist_id}")
    suspend fun getArtistDetails(
        @Path("artist_id") artistId: Int
    ): Response<ArtistDetailsResponse>
}