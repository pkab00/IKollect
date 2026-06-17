package com.vbshkn.ikollect.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vbshkn.ikollect.data.local.dao.AlbumDao
import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.dao.CrossRefDao
import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.dao.TagDao
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import com.vbshkn.ikollect.data.local.model.entity.TagEntity

@Database(
    entities = [
        ArtistEntity::class,
        AlbumEntity::class,
        PhotocardEntity::class,
        TagEntity::class,
        AlbumArtistCrossRef::class,
        PhotocardArtistCrossRef::class,
        ArtistArtistCrossRef::class,
        PhotocardTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun photocardDao(): PhotocardDao
    abstract fun tagDao(): TagDao
    abstract fun crossRefDao(): CrossRefDao

    companion object {
        const val DATABASE_NAME = "ikollect_db"
        val TRIGGER_ALBUMS_SOFT_DELETE_CASCADE: String = """
                            CREATE TRIGGER IF NOT EXISTS albums_soft_delete_cascade
                            AFTER UPDATE OF isDeleted ON AlbumEntity
                            FOR EACH ROW WHEN NEW.isDeleted = 1
                            BEGIN
                                UPDATE AlbumArtistCrossRef
                                SET isDeleted = 1, isSynchronized = 0 WHERE albumId = OLD.albumId;
                            END;
                        """.trimIndent()
        val TRIGGER_PHOTOCARDS_SOFT_DELETE_CASCADE = """
                            CREATE TRIGGER IF NOT EXISTS photocard_soft_delete_cascade
                            AFTER UPDATE OF isDeleted ON PhotocardEntity
                            FOR EACH ROW WHEN NEW.isDeleted = 1
                            BEGIN
                                UPDATE PhotocardArtistCrossRef
                                SET isDeleted = 1, isSynchronized = 0 WHERE photocardId = OLD.photocardId;
                                UPDATE PhotocardTagCrossRef
                                SET isDeleted = 1, isSynchronized = 0 WHERE photocardId = OLD.photocardId;
                            END;
                        """.trimIndent()

        val TRIGGER_ARTISTS_SOFT_DELETE_CASCADE = """
                            CREATE TRIGGER IF NOT EXISTS artists_soft_delete_cascade
                            AFTER UPDATE OF isDeleted ON ArtistEntity
                            FOR EACH ROW WHEN NEW.isDeleted = 1
                            BEGIN
                                UPDATE ArtistArtistCrossRef
                                SET isDeleted = 1, isSynchronized = 0 WHERE groupId = OLD.artistId OR memberId = OLD.artistId;
                                UPDATE AlbumArtistCrossRef
                                SET isDeleted = 1, isSynchronized = 0 WHERE artistId = OLD.artistId;
                                UPDATE PhotocardArtistCrossRef
                                SET isDeleted = 1, isSynchronized = 0 WHERE artistId = OLD.artistId;
                            END;
                        """.trimIndent()
        val TRIGGER_UPDATE_TAGS_SOFT_DELETE_CASCADE = """
                            CREATE TRIGGER IF NOT EXISTS update_tags_soft_delete_cascade
                            AFTER UPDATE OF isDeleted ON TagEntity
                            FOR EACH ROW WHEN NEW.isDeleted = 1 AND NEW.isSystemTag = 0
                            BEGIN
                                UPDATE PhotocardTagCrossRef
                                SET isDeleted = 1, isSynchronized = 0 
                                WHERE tagId = OLD.tagId;
                            END;
                        """.trimIndent()
        val TRIGGER_INSERT_TAGS_SOFT_DELETE_CASCADE = """
                            CREATE TRIGGER IF NOT EXISTS insert_tags_soft_delete_cascade
                            AFTER INSERT ON PhotocardTagCrossRef
                            FOR EACH ROW 
                            WHEN EXISTS (SELECT 1 FROM PhotocardEntity WHERE photocardId = NEW.photocardId AND isDeleted = 1)
                            BEGIN
                                UPDATE PhotocardTagCrossRef 
                                SET isDeleted = 1, isSynchronized = 0 
                                WHERE rowid = NEW.rowid;
                            END;
                        """.trimIndent()
    }
}