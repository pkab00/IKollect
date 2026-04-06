package com.vbshkn.ikollect.data.mapper

import androidx.annotation.StringRes
import com.vbshkn.ikollect.R

enum class SystemTag(
    val slug: String,
    @StringRes val redId: Int
) {
    POB("tag_pob", R.string.tag_pob),
    AUTOGRAPH("tag_autograph", R.string.tag_autograph),
    POLAROID("tag_polaroid", R.string.tag_polaroid),
    LUCKY_DRAW("tag_lucky_draw", R.string.tag_lucky_draw),
    MEMBERSHIP("tag_membership", R.string.tag_membership),
    CONCERT("tag_concert", R.string.tag_concert),
    FAN_MADE("tag_fan_made", R.string.tag_fan_made),
    DAMAGED("tag_damaged", R.string.tag_damaged),
    DUPLICATE("tag_duplicate", R.string.tag_duplicate),
    UNIT("tag_unit", R.string.tag_unit);

    companion object {
        fun getResId(slug: String): Int? {
            return entries.find { it.slug == slug }?.redId
        }
    }
}