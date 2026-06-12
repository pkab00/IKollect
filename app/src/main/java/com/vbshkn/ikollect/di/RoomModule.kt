package com.vbshkn.ikollect.di

import com.vbshkn.ikollect.data.local.database.AppDatabase
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vbshkn.ikollect.data.local.dao.AlbumDao
import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.dao.CrossRefDao
import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.dao.TagDao
import com.vbshkn.ikollect.data.local.model.entity.TagEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    lateinit var database: AppDatabase
    val systemTags =
        listOf(
            "tag_pob" to 0xFFFF8A80,          // Soft Coral
            "tag_membership" to 0xFFFFAB91,   // Muted Deep Orange
            "tag_autograph" to 0xFF9FA8DA,    // Pastel Indigo
            "tag_concert" to 0xFF90CAF9,      // Soft Sky Blue
            "tag_polaroid" to 0xFFCE93D8,     // Soft Lilac
            "tag_fan_made" to 0xFFF48FB1,     // Soft Pink
            "tag_lucky_draw" to 0xFFA5D6A7,   // Pale Sage Green
            "tag_unit" to 0xFF80CBC4,         // Muted Teal
            "tag_damaged" to 0xFFCFD8DC,      // Blue Grey Light
            "tag_duplicate" to 0xFFB0BEC5     // Muted Slate
        ).mapIndexed { index, pair ->
                TagEntity(
                    tagId = -(index + 1).toLong(),
                    isSystemTag = true,
                    tagName = pair.first,
                    tagColor = pair.second,
                    isSynchronized = true
                )
            }

    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ikollect_db"
        )
            .fallbackToDestructiveMigration(true)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    GlobalScope.launch {
                        val tagDao = database.tagDao()
                        systemTags.forEach { tagDao.insert(it) }
                    }
                    db.execSQL(AppDatabase.TRIGGER_ALBUMS_SOFT_DELETE_CASCADE)
                    db.execSQL(AppDatabase.TRIGGER_PHOTOCARDS_SOFT_DELETE_CASCADE)
                    db.execSQL(AppDatabase.TRIGGER_ARTISTS_SOFT_DELETE_CASCADE)
                }
            })
            .build()
        return database
    }

    @Provides
    fun provideArtistDao(db: AppDatabase): ArtistDao = db.artistDao()

    @Provides
    fun provideAlbumDao(db: AppDatabase): AlbumDao = db.albumDao()

    @Provides
    fun providePhotocardDao(db: AppDatabase): PhotocardDao = db.photocardDao()

    @Provides
    fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()

    @Provides
    fun provideCrossRefDao(db: AppDatabase): CrossRefDao = db.crossRefDao()
}