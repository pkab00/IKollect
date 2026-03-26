package com.vbshkn.ikollect.util

object ArtistNameHelper {
    fun pickBestNameOption(
        apiName: String,
        nameVariations: List<String>?
    ): String {
        val bestName = cleanApiName(apiName)
        return bestName
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