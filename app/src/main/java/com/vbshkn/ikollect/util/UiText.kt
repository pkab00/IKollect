package com.vbshkn.ikollect.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(@StringRes val resId: Int, vararg val args: Any) : UiText()
    data class Composite(val items: List<UiText>) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
            is Composite -> {
                val stringBuilder = StringBuilder()
                items.forEach { item ->
                    if (stringBuilder.isNotEmpty()) {
                        stringBuilder.append(" ")
                    }
                    stringBuilder.append(item.asString())
                }
                stringBuilder.toString()
            }
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
            is Composite -> {
                val stringBuilder = StringBuilder()
                items.forEach { item ->
                    if (stringBuilder.isNotEmpty()) {
                        stringBuilder.append(" ")
                    }
                    stringBuilder.append(item.asString(context))
                }
                stringBuilder.toString()
            }
        }
    }

    operator fun plus(other: UiText): UiText {
        val currentItems = if (this is Composite) this.items else listOf(this)
        val nextItems = if (other is Composite) other.items else listOf(other)
        return Composite(currentItems + nextItems)
    }
}