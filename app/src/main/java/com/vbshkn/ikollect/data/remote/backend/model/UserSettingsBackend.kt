package com.vbshkn.ikollect.data.remote.backend.model

import com.vbshkn.ikollect.data.local.datastore.LocalSettings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsBackend (
    @SerialName("id")
    val id: String? = null,
    @SerialName("settings_json")
    val settings: LocalSettings? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false
)