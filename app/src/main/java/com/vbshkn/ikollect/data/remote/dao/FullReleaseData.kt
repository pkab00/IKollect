package com.vbshkn.ikollect.data.remote.dao

data class FullReleaseData(
    val barcode: String,
    val searchResult: SearchResult,
    val releaseDetailsResponse: ReleaseDetailsResponse,
    val masterDetailsResponse: MasterDetailsResponse,
    val artistDetailsResponses: List<ArtistDetailsResponse>,
    val availableVersions: List<Pair<String?, List<FormatDao>?>>
)
