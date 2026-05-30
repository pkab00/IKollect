package com.vbshkn.ikollect.domain.repository

interface ImageRepository {
    fun saveToInternalStorage(uriString: String): String
    fun deleteFromInternalStorage(uriString: String)
    fun clearLocalStorage()
}