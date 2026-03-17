package com.vbshkn.ikollect.data.remote.dao

import com.google.gson.annotations.SerializedName

data class MasterDetailsResponse (
    @SerializedName("styles")
    val styles: List<String>
)