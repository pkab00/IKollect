package com.vbshkn.ikollect.presentation.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.vbshkn.ikollect.domain.model.candidate.AlbumCandidate
import kotlinx.serialization.json.Json

object AlbumCandidateType : NavType<AlbumCandidate>(isNullableAllowed = false) {
    override fun put(
        bundle: SavedState,
        key: String,
        value: AlbumCandidate
    ) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(
        bundle: SavedState,
        key: String
    ): AlbumCandidate? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): AlbumCandidate {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: AlbumCandidate): String {
        return Uri.encode(Json.encodeToString(value))
    }
}