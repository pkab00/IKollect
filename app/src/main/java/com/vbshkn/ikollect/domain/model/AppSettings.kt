package com.vbshkn.ikollect.domain.model

import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations

data class AppSettings (
    val language: String,
    val theme: Int,
    val navBarDestinations: List<NavBarDestinations>
)