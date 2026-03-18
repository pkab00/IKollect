package com.vbshkn.ikollect.presentation.navigation

import com.vbshkn.ikollect.R

enum class NavBarDestinations(
    val labelRes: Int,
    val iconRes: Int,
    val route: Route
) {
    ALBUMS(R.string.navigation_albums, R.drawable.ic_albums, Route.Albums),
    PHOTOCARDS(R.string.navigation_photocards, R.drawable.ic_photocards, Route.Photocards),
    ACCOUNT(R.string.navigation_account, R.drawable.ic_account, Route.Account)
}