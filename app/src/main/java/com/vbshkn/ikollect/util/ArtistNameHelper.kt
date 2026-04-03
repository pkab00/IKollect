package com.vbshkn.ikollect.util

import kotlin.collections.emptyList

object ArtistNameHelper {
    fun pickBestNameOption(
        apiName: String,
        nameVariations: List<String>?,
        isGroupName: Boolean
    ): String {
        val allLatinRegex = Regex("""[\p{IsLatin}\s.\-()&]+""")

        val allLatinNames = listOf(cleanApiName(apiName)) + (nameVariations ?: emptyList())
            .filter { it.matches(allLatinRegex) }
            .sortedByDescending { it.length }
        val (singleWord, multipleWords) = allLatinNames
            .partition { it.split(" ").size == 1 }

        if (allLatinNames.isEmpty()) return cleanApiName(apiName)

        if (singleWord.isNotEmpty()) {
            val optimalOption = singleWord
                .find {
                    if (isGroupName || it.length <= 3) { it.all { c -> c.isUpperCase() } }
                    else { it[0].isUpperCase() && it.drop(1).all { c -> c.isLowerCase() } }
                }
            return (optimalOption ?: singleWord.first()).trim()
        }
        return multipleWords.first().trim()
    }

    private fun cleanApiName(name: String): String {
        val garbageRegex = Regex("""\s\(\d+\)""")

        val cleanedName = name
            .replace(
                regex = garbageRegex,
                replacement = ""
            )
            .trim()

        return cleanedName
    }
}