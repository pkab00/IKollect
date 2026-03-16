package com.vbshkn.ikollect.data.remote.dao

data class FullReleaseData(
    val barcode: String,
    val searchResult: SearchResult,
    val releaseDetailsResponse: ReleaseDetailsResponse,
    val artistDetailsResponses: List<ArtistDetailsResponse>,
    val availableCovers: List<String>
)
