package com.vbshkn.ikollect.data

import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.dao.AlbumDao
import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.dao.CrossRefDao
import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.dao.TagDao
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.model.entity.AlbumArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.AlbumEntity
import com.vbshkn.ikollect.data.local.model.entity.ArtistArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.ArtistEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardArtistCrossRef
import com.vbshkn.ikollect.data.local.model.entity.PhotocardEntity
import com.vbshkn.ikollect.data.local.model.entity.PhotocardTagCrossRef
import com.vbshkn.ikollect.data.local.model.entity.TagEntity
import com.vbshkn.ikollect.data.mapper.BackendMappers.toBackend
import com.vbshkn.ikollect.data.mapper.BackendMappers.toEntity
import com.vbshkn.ikollect.data.remote.backend.BackendTables
import com.vbshkn.ikollect.data.remote.backend.dao.BackendStorageDao
import com.vbshkn.ikollect.data.remote.backend.model.AlbumArtistCrossRefBackend
import com.vbshkn.ikollect.data.remote.backend.model.GlobalArtistBackend
import com.vbshkn.ikollect.data.remote.backend.model.GlobalArtistHierarchyBackend
import com.vbshkn.ikollect.data.remote.backend.model.PhotocardArtistCrossRefBackend
import com.vbshkn.ikollect.data.remote.backend.model.PhotocardTagCrossRefBackend
import com.vbshkn.ikollect.data.remote.backend.model.TagBackend
import com.vbshkn.ikollect.data.remote.backend.model.UserAlbumBackend
import com.vbshkn.ikollect.data.remote.backend.model.UserArtistSettingsBackend
import com.vbshkn.ikollect.data.remote.backend.model.UserPhotocardBackend
import com.vbshkn.ikollect.di.ApplicationScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

private const val TAG = "SyncManager"

class SyncManager @Inject constructor(
    private val database: AppDatabase,
    private val supabase: SupabaseClient,
    @ApplicationScope private val scope: CoroutineScope,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val photocardDao: PhotocardDao,
    private val tagDao: TagDao,
    private val crossRefDao: CrossRefDao,
    private val backendStorageDao: BackendStorageDao
) {
    fun performInitialSync() = scope.launch {
        supabase.auth.awaitInitialization()
        if (supabase.auth.currentSessionOrNull() == null) {
            android.util.Log.d(TAG, "Current session is null")
            return@launch
        }

        val user = supabase.auth.currentUserOrNull() ?: return@launch
        val userId = user.id

        val albumsCount = supabase.from(BackendTables.USER.ALBUMS)
            .select(columns = Columns.raw("count")) {
                filter { eq("user_id", userId) }
            }.decodeSingle<CountResponse>().count
        val photocardsCount = supabase.from(BackendTables.USER.PHOTOCARDS)
            .select(columns = Columns.raw("count")) {
                filter { eq("user_id", userId) }
            }.decodeSingle<CountResponse>().count
        val noDataOnBackend = albumsCount == 0L && photocardsCount == 0L

        if (noDataOnBackend) {
            android.util.Log.d(TAG, "No data on backend. Considering to upload local data")
            uploadAllToBackend(userId)
        }
        else {
            android.util.Log.d(TAG, "Considering to download data from backend")
            downloadAllFromBackend()
        }
    }

    private suspend fun downloadAllFromBackend() {
        // DOWNLOADING FROM REMOTE
        val backPackage = try {
            coroutineScope {
                val backendArtists = supabase.from(BackendTables.GLOBAL.ARTISTS).select().decodeList<GlobalArtistBackend>()
                val backendArtistHierarchy = supabase.from(BackendTables.GLOBAL.ARTIST_HIERARCHY).select().decodeList<GlobalArtistHierarchyBackend>()
                val backendAlbums = supabase.from(BackendTables.USER.ALBUMS).select().decodeList<UserAlbumBackend>()
                val backendArtistSettings = supabase.from(BackendTables.USER.ARTIST_SETTINGS).select().decodeList<UserArtistSettingsBackend>()
                val backendPhotocards = supabase.from(BackendTables.USER.PHOTOCARDS).select().decodeList<UserPhotocardBackend>()
                val backendAACrossRef = supabase.from(BackendTables.CROSSREF.ALBUM_ARTIST).select().decodeList<AlbumArtistCrossRefBackend>()
                val backendPACrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST).select().decodeList<PhotocardArtistCrossRefBackend>()
                val backendPTCrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG).select().decodeList<PhotocardTagCrossRefBackend>()
                val backendTags = supabase.from(BackendTables.TAGS).select().decodeList<TagBackend>()

                BackendDataPackage(backendArtists, backendArtistHierarchy, backendAlbums, backendArtistSettings, backendPhotocards, backendAACrossRef, backendPACrossRef, backendPTCrossRef, backendTags)
            }
        } catch (e: Exception) {
            android.util.Log.d(TAG, "Failed to download data from remote: ", e)
            return
        }
        // MAPPING TO LOCAL ENTITIES
        val settingsMap = backPackage.artistSettings.associateBy { it.artistId }
        val localArtists = backPackage.artists.mapNotNull { artist ->
            val settings = settingsMap[artist.artistId]
            settings?.let { artist.toEntity(it) }
        }
        val localTags = backPackage.tags.map { it.toEntity() }
        val localAlbums = backPackage.albums.map { it.toEntity() }
        val localPhotocards = backPackage.photocards.map { it.toEntity() }
        val localArtistArtistCF = backPackage.artistHierarchy.map { it.toEntity() }
        val localAlbumArtistCF = backPackage.albumArtistCR.map { it.toEntity() }
        val localPhotocardArtistCF = backPackage.photocardArtistCR.map { it.toEntity() }
        val localPhotocardTagCF = backPackage.photocardTagCR.map { it.toEntity() }

        database.withTransaction {
            database.openHelper.writableDatabase.execSQL("PRAGMA foreign_keys = OFF")
            try {
                // CLEANING THE LOCAL DB
                crossRefDao.clearAll()
                photocardDao.clearAll()
                albumDao.clearAll()
                artistDao.clearAll()
                tagDao.clearAll()

                // INSERT TO THE LOCAL DATABASE
                artistDao.insertAll(localArtists)
                tagDao.insertAll(localTags)
                albumDao.insertAll(localAlbums)
                photocardDao.insertAll(localPhotocards)
                artistDao.insertGroupLinks(localArtistArtistCF)
                albumDao.insertArtistLinks(localAlbumArtistCF)
                photocardDao.insertArtistLinks(localPhotocardArtistCF)
                tagDao.insertTagLinks(localPhotocardTagCF)
                android.util.Log.d(TAG, "Download from remote completed")

            } finally {
                database.openHelper.writableDatabase.execSQL("PRAGMA foreign_keys = ON")
            }
        }
    }

    private suspend fun uploadAllToBackend(userId: String) {
        // LOADING LOCAL DATA
        val localPackage = try {
            database.withTransaction {
                val localAlbumArtist = crossRefDao.getAlbumArtist().first()
                val localArtistArtist = crossRefDao.getArtistArtist().first()
                val localPhotocardArtist = crossRefDao.getPhotocardArtist().first()
                val localPhotocardTag = crossRefDao.getPhotocardTag().first()
                val localAlbums = albumDao.getAll().first()
                val localArtists = artistDao.getAll().first()
                val localPhotocards = photocardDao.getAll().first()
                val localTags = tagDao.getAll().first()

                LocalDataPackage(localAlbumArtist, localArtistArtist, localPhotocardArtist, localPhotocardTag, localAlbums, localArtists, localPhotocards, localTags)
            }
        } catch (e: Exception) {
            android.util.Log.d(TAG,"Failed to download local data: ", e)
            return
        }

        // COPYING LOCAL IMAGES
        val modifiedPackage = localPackage.copy(
            albums = localPackage.albums.map {
                if (it.imageUrl?.startsWith("file:///") == true) {
                    val newPath = backendStorageDao.upsertAlbumImage(it.imageUrl, it.albumId)
                    val copy = it.copy(imageUrl = newPath ?: it.imageUrl)
                    albumDao.updateAlbum(copy)
                    copy
                } else { it }
            },
            photocards = localPackage.photocards.map {
                if (it.imageUrl?.startsWith("file:///") == true) {
                    val newPath = backendStorageDao.upsertPhotocardImage(it.imageUrl, it.photocardId)
                    val copy = it.copy(imageUrl = newPath ?: it.imageUrl)
                    photocardDao.updatePhotocard(copy)
                    copy
                } else { it }
            }
        )

        // MAPPING TO REMOTE
        val backendAlbumArtistCR = modifiedPackage.albumArtistCR.map { it.toBackend(userId) }
        val backendPhotocardArtistCR = modifiedPackage.photocardArtistCR.map { it.toBackend(userId) }
        val backendPhotocardTagCR = modifiedPackage.photocardTagCR.map { it.toBackend(userId) }
        val backendArtistHierarchy = modifiedPackage.artistArtistCR.map { it.toBackend() }
        val backendArtist = modifiedPackage.artists.map { it.toBackend() }
        val backendArtistSettings = modifiedPackage.artists.map { it.toBackend(userId) }
        val backendAlbums = modifiedPackage.albums.map { it.toBackend(userId) }
        val backendPhotocards = modifiedPackage.photocards.map { it.toBackend(userId) }
        val backendTags = modifiedPackage.tags.filter { !it.isSystemTag }.map { it.toBackend(userId) }

        // UPLOADING TO REMOTE
        try {
            if (backendArtist.isNotEmpty()) {
                supabase.from(BackendTables.GLOBAL.ARTISTS).upsert(backendArtist)
            }
            if (backendArtistSettings.isNotEmpty()) {
                supabase.from(BackendTables.USER.ARTIST_SETTINGS).insert(backendArtistSettings)
            }
            if (backendAlbums.isNotEmpty()) {
                supabase.from(BackendTables.USER.ALBUMS).insert(backendAlbums)
            }
            if (backendPhotocards.isNotEmpty()) {
                supabase.from(BackendTables.USER.PHOTOCARDS).insert(backendPhotocards)
            }
            if (backendTags.isNotEmpty()) {
                supabase.from(BackendTables.TAGS).insert(backendTags)
            }
            if (backendAlbumArtistCR.isNotEmpty()) {
                supabase.from(BackendTables.CROSSREF.ALBUM_ARTIST).insert(backendAlbumArtistCR)
            }
            if (backendPhotocardArtistCR.isNotEmpty()) {
                supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST).insert(backendPhotocardArtistCR)
            }
            if (backendPhotocardTagCR.isNotEmpty()) {
                supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG).insert(backendPhotocardTagCR)
            }
            if (backendArtistHierarchy.isNotEmpty()) {
                supabase.from(BackendTables.GLOBAL.ARTIST_HIERARCHY).upsert(backendArtistHierarchy)
            }
            android.util.Log.d(TAG, "Upload completed successfully")
        } catch (e: Exception) {
            android.util.Log.d(TAG, "Upload failed: ", e)
        }
    }
}

@Serializable data class CountResponse(val count: Long)

data class BackendDataPackage(
    val artists: List<GlobalArtistBackend>,
    val artistHierarchy: List<GlobalArtistHierarchyBackend>,
    val albums: List<UserAlbumBackend>,
    val artistSettings: List<UserArtistSettingsBackend>,
    val photocards: List<UserPhotocardBackend>,
    val albumArtistCR: List<AlbumArtistCrossRefBackend>,
    val photocardArtistCR: List<PhotocardArtistCrossRefBackend>,
    val photocardTagCR: List<PhotocardTagCrossRefBackend>,
    val tags: List<TagBackend>
)

data class LocalDataPackage(
    val albumArtistCR: List<AlbumArtistCrossRef>,
    val artistArtistCR: List<ArtistArtistCrossRef>,
    val photocardArtistCR: List<PhotocardArtistCrossRef>,
    val photocardTagCR: List<PhotocardTagCrossRef>,
    val albums: List<AlbumEntity>,
    val artists: List<ArtistEntity>,
    val photocards: List<PhotocardEntity>,
    val tags: List<TagEntity>
)