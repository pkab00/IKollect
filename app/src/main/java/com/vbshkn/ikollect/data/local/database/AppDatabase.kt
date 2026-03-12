package com.vbshkn.ikollect.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vbshkn.ikollect.data.local.dao.AlbumDao
import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.entity.PhotocardEntity

@Database(
    entities = [
        ArtistEntity::class,
        AlbumEntity::class,
        PhotocardEntity::class,
        AlbumArtistCrossRef::class,
        PhotocardArtistCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun photocardDao(): PhotocardDao
}