package com.vbshkn.ikollect.presentation.navigation

import com.vbshkn.ikollect.R

enum class AppDestinations(
    val labelRes: Int,
    val iconRes: Int,
    val route: String
) {
    ALBUMS(R.string.navigation_albums, R.drawable.ic_albums, "albums"),
    PHOTOCARDS(R.string.navigation_photocards, R.drawable.ic_photocards, "photocards"),
    ACCOUNT(R.string.navigation_account, R.drawable.ic_account, "account")
}

object AddAlbumFlow {
    const val ROOT = "add_album"
    const val DETAILS = "add_album/details"
    const val SELECT_VERSION = "add_album/select_version"
    const val ADD_NOTES = "add_album/add_notes"
}