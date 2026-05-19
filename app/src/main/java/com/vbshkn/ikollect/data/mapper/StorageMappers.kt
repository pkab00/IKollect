package com.vbshkn.ikollect.data.mapper

import androidx.appcompat.app.AppCompatDelegate
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.data.local.datastore.LocalLanguage
import com.vbshkn.ikollect.data.local.datastore.LocalSettings
import com.vbshkn.ikollect.data.local.datastore.LocalTabs
import com.vbshkn.ikollect.data.local.datastore.LocalTheme
import com.vbshkn.ikollect.domain.model.AppSettings
import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations
import com.vbshkn.ikollect.util.UiText

fun LocalTabs.toDomain(): NavBarDestinations {
    return when (this) {
        LocalTabs.ALBUMS -> NavBarDestinations.ALBUMS
        LocalTabs.PHOTOCARDS -> NavBarDestinations.PHOTOCARDS
        LocalTabs.ARTISTS -> NavBarDestinations.ARTISTS
        LocalTabs.PROFILE -> NavBarDestinations.PROFILE
    }
}

fun NavBarDestinations.toData(): LocalTabs {
    return when (this) {
        NavBarDestinations.ALBUMS -> LocalTabs.ALBUMS
        NavBarDestinations.PHOTOCARDS -> LocalTabs.PHOTOCARDS
        NavBarDestinations.ARTISTS -> LocalTabs.ARTISTS
        NavBarDestinations.PROFILE -> LocalTabs.PROFILE
    }
}

fun LocalLanguage.toDomain(): String {
    return this.code
}

fun LocalTheme.toDomain(): Int {
    return when (this) {
        LocalTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        LocalTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        LocalTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
}

fun LocalTheme.toUiText(): UiText {
    return when (this) {
        LocalTheme.LIGHT -> UiText.StringResource(R.string.theme_light)
        LocalTheme.DARK -> UiText.StringResource(R.string.theme_dark)
        LocalTheme.SYSTEM -> UiText.StringResource(R.string.theme_system)
    }
}

fun LocalLanguage.toUiText(): UiText {
    return when (this) {
        LocalLanguage.ENGLISH -> UiText.StringResource(R.string.language_english)
        LocalLanguage.RUSSIAN -> UiText.StringResource(R.string.language_russian)
        LocalLanguage.SYSTEM -> UiText.StringResource(R.string.language_system)
    }
}

fun LocalSettings.toDomain(): AppSettings {
    return AppSettings(
        language = this.language.toDomain(),
        theme = this.theme.toDomain(),
        navBarDestinations = this.tabsOrder.map { it.toDomain() }
    )
}