package com.vbshkn.ikollect.data.local.datastore

import com.vbshkn.ikollect.util.now
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalSettings(
    @SerialName("theme")
    val theme: LocalTheme = LocalTheme.SYSTEM,
    @SerialName("language")
    val language: LocalLanguage = LocalLanguage.SYSTEM,
    @SerialName("tabs_order")
    val tabsOrder: List<LocalTabs> = LocalTabs.entries
)

@Serializable
enum class LocalTheme {
    @SerialName("light") LIGHT,
    @SerialName("dark") DARK,
    @SerialName("system") SYSTEM
}

@Serializable
enum class LocalLanguage(val code: String) {
    @SerialName("en") ENGLISH("en"),
    @SerialName("ru") RUSSIAN("ru"),
    @SerialName("sys") SYSTEM("")
}

@Serializable
enum class LocalTabs {
    @SerialName("albums") ALBUMS,
    @SerialName("photocards") PHOTOCARDS,
    @SerialName("artists") ARTISTS,
    @SerialName("profile") PROFILE
}
