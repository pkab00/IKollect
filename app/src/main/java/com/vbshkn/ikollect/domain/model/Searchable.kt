package com.vbshkn.ikollect.domain.model

interface Searchable {
    fun matchesQuery(query: String) : Boolean
}

fun <T : Searchable> List<T>.getMatching(query: String) : List<T> {
    return this.filter { it.matchesQuery(query) }
}