package com.vbshkn.ikollect.data.remote.dao

data class FullReleaseData(
    val searchResult: SearchResult,
    val detailsResponse: ReleaseDetailsResponse,
    val artists: List<ArtistDetailsResponse>
)
