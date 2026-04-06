package com.vbshkn.ikollect.di

import com.vbshkn.ikollect.data.local.database.AppDatabase
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vbshkn.ikollect.data.local.dao.AlbumDao
import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.dao.TagDao
import com.vbshkn.ikollect.data.local.model.entity.TagEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    lateinit var database: AppDatabase
    val systemTags =
        listOf(
            "tag_pob" to 0xFF990000, "tag_autograph" to 0xFF0000CC,
            "tag_polaroid" to 0xFFCC00CC, "tag_lucky_draw" to 0xFF009933,
            "tag_membership" to 0xFF990000, "tag_concert" to 0xFF0000CC,
            "tag_fan_made" to 0xFFCC00CC, "tag_damaged" to 0xFF666666,
            "tag_duplicate" to 0xFF666666, "tag_unit" to 0xFF009933
        ).mapIndexed { index, pair ->
                TagEntity(
                    tagId = (index + 1).toLong(),
                    isSystemTag = true,
                    tagName = pair.first,
                    tagColor = Color(pair.second)
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
}