package com.vbshkn.ikollect.data.background

import android.util.Log
import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.dao.AlbumDao
import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.dao.CrossRefDao
import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.dao.TagDao
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.datastore.ServiceLogStorage
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
import com.vbshkn.ikollect.data.mapper.toTimeMillis
import com.vbshkn.ikollect.data.mapper.toTimestamptz
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
import kotlin.math.max
import kotlin.time.Instant

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
    private val backendStorageDao: BackendStorageDao,
    private val serviceLogStorage: ServiceLogStorage
) {
    fun performInitialSync() = scope.launch {
        supabase.auth.awaitInitialization()
        if (supabase.auth.currentSessionOrNull() == null) {
            Log.d(TAG, "Current session is null")
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
            Log.d(TAG, "No data on backend. Considering to upload local data")
            uploadAllToBackend(userId)
        } else {
            Log.d(TAG, "Considering to download data from backend")
            downloadAllFromBackend()
        }
    }

    private suspend fun downloadAllFromBackend() {
        // DOWNLOADING FROM REMOTE
        val backPackage = try {
            coroutineScope {
                val backendArtistSettings = supabase.from(BackendTables.USER.ARTIST_SETTINGS).select().decodeList<UserArtistSettingsBackend>()
                val userArtistIds = backendArtistSettings.map { it.artistId }
                val backendArtists = supabase.from(BackendTables.GLOBAL.ARTISTS)
                    .select {
                        filter { GlobalArtistBackend::artistId isIn userArtistIds }
                    }
                    .decodeList<GlobalArtistBackend>()
                val backendArtistHierarchy = supabase.from(BackendTables.GLOBAL.ARTIST_HIERARCHY)
                    .select {
                        filter {
                            and {
                                GlobalArtistHierarchyBackend::groupId isIn userArtistIds
                                GlobalArtistHierarchyBackend::memberId isIn userArtistIds
                            }
                        }
                    }
                    .decodeList<GlobalArtistHierarchyBackend>()
                val backendAlbums = supabase.from(BackendTables.USER.ALBUMS).select().decodeList<UserAlbumBackend>()
                val backendPhotocards = supabase.from(BackendTables.USER.PHOTOCARDS).select().decodeList<UserPhotocardBackend>()
                val backendAACrossRef = supabase.from(BackendTables.CROSSREF.ALBUM_ARTIST).select().decodeList<AlbumArtistCrossRefBackend>()
                val backendPACrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST).select().decodeList<PhotocardArtistCrossRefBackend>()
                val backendPTCrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG).select().decodeList<PhotocardTagCrossRefBackend>()
                val backendTags = supabase.from(BackendTables.TAGS).select().decodeList<TagBackend>()

                BackendDataPackage(
                    backendArtists,
                    backendArtistHierarchy,
                    backendAlbums,
                    backendArtistSettings,
                    backendPhotocards,
                    backendAACrossRef,
                    backendPACrossRef,
                    backendPTCrossRef,
                    backendTags
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to download data from remote: ", e)
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
                artistDao.upsertGroupLinks(localArtistArtistCF)
                albumDao.upsertArtistLinks(localAlbumArtistCF)
                photocardDao.upsertArtistLinks(localPhotocardArtistCF)
                tagDao.upsertTagLinks(localPhotocardTagCF)
                Log.d(TAG, "Download from remote completed")

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

                LocalDataPackage(
                    localAlbumArtist,
                    localArtistArtist,
                    localPhotocardArtist,
                    localPhotocardTag,
                    localAlbums,
                    localArtists,
                    localPhotocards,
                    localTags
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to download local data: ", e)
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
                } else {
                    it
                }
            },
            photocards = localPackage.photocards.map {
                if (it.imageUrl?.startsWith("file:///") == true) {
                    val newPath =
                        backendStorageDao.upsertPhotocardImage(it.imageUrl, it.photocardId)
                    val copy = it.copy(imageUrl = newPath ?: it.imageUrl)
                    photocardDao.updatePhotocard(copy)
                    copy
                } else {
                    it
                }
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
                supabase.from(BackendTables.USER.ARTIST_SETTINGS).upsert(backendArtistSettings)
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
            Log.d(TAG, "Upload completed successfully")
        } catch (e: Exception) {
            Log.d(TAG, "Upload failed: ", e)
        }
    }

    // ================================ HANDSHAKE =================================================

    suspend fun performHandshake(userId: String) {
        Log.i(TAG, "Starting scheduled handshake...")
        val startPoint = now()
        val lastSyncTimestamp = serviceLogStorage.getLastSyncTimestamp().first()
            ?: Instant.fromEpochMilliseconds(0).toString()
        Log.d(TAG, "Last sync at $lastSyncTimestamp")

        Log.i(TAG, "Step 1: Local -> Remote")
        uploadLocalChanges(userId)
        Log.i(TAG, "Step 2: Local <- Remote")
        val finishTimestamp = downloadRemoteChanges(lastSyncTimestamp)
        Log.i(TAG, "Step 3: Clear Deleted")
        clearDeleted()

        // Updating last sync timestamp
        // Decreasing it a lil bit just to avoid blind zone
        val finishPoint = now()
        val totalSeconds = (finishPoint - startPoint) / 1000f
        finishTimestamp?.let {
            serviceLogStorage.updateLastSyncTimestamp(finishTimestamp.toTimestamptz())
            Log.i(TAG, "Handshake finished at ${finishTimestamp.toTimestamptz()}\nTook $totalSeconds seconds to perform")
        }
    }

    private suspend fun uploadLocalChanges(userId: String) {
        val localPackage = try {
            database.withTransaction {
                val localAlbumArtist = crossRefDao.getAlbumArtistUnSynchronized().first()
                val localArtistArtist = crossRefDao.getArtistArtistUnSynchronized().first()
                val localPhotocardArtist = crossRefDao.getPhotocardArtistUnSynchronized().first()
                val localPhotocardTag = crossRefDao.getPhotocardTagUnSynchronized().first()
                val localAlbums = albumDao.getUnSynchronized().first()
                val localArtists = artistDao.getUnSynchronized().first()
                val localPhotocards = photocardDao.getUnSynchronized().first()
                val localTags = tagDao.getUnSynchronized().first()

                LocalDataPackage(localAlbumArtist, localArtistArtist, localPhotocardArtist, localPhotocardTag, localAlbums, localArtists, localPhotocards, localTags)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to download local data: ", e)
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
        val backendTags = modifiedPackage.tags.map { it.toBackend(userId) }

        // SYNCHRONIZING
        if (backendArtist.isNotEmpty() || backendArtistSettings.isNotEmpty() || backendArtistHierarchy.isNotEmpty()) {
            try {
                val artistsUpd = supabase.from(BackendTables.GLOBAL.ARTISTS).upsert(backendArtist) { select() }
                    .decodeList<GlobalArtistBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val artistSettingsUpd = supabase.from(BackendTables.USER.ARTIST_SETTINGS).upsert(backendArtistSettings) { select() }
                    .decodeList<UserArtistSettingsBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val hierarchyUpd = supabase.from(BackendTables.GLOBAL.ARTIST_HIERARCHY).upsert(backendArtistHierarchy) { select() }
                    .decodeList<GlobalArtistHierarchyBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                database.withTransaction {
                    artistDao.updateAll(localPackage.artists.map { it.copy(isSynchronized = true, updatedAt = max(artistsUpd, artistSettingsUpd)) })
                    crossRefDao.updateArtistArtist(localPackage.artistArtistCR.map { it.copy(isSynchronized = true, updatedAt = hierarchyUpd) })
                }
                Log.d(TAG, "Artists were synchronized as planned, local flags updated")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to update artists: ", e)
            }
        }
        if (backendAlbums.isNotEmpty() || backendAlbumArtistCR.isNotEmpty()) {
            try {
                val albumsUpd = supabase.from(BackendTables.USER.ALBUMS).upsert(backendAlbums) { select() }
                    .decodeList<UserAlbumBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val albumArtistUpd = supabase.from(BackendTables.CROSSREF.ALBUM_ARTIST).upsert(backendAlbumArtistCR) { select() }
                    .decodeList<AlbumArtistCrossRefBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                database.withTransaction {
                    albumDao.updateAll(modifiedPackage.albums.map { it.copy(isSynchronized = true, updatedAt = albumsUpd) })
                    crossRefDao.updateAlbumArtist(localPackage.albumArtistCR.map { it.copy(isSynchronized = true, updatedAt = albumArtistUpd) })
                }
                Log.d(TAG, "Albums were synchronized as planned, local flags updated")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to update albums: ", e)
            }
        }
        if (backendPhotocards.isNotEmpty() || backendPhotocardArtistCR.isNotEmpty()) {
            try {
                val photocardsUpd = supabase.from(BackendTables.USER.PHOTOCARDS).upsert(backendPhotocards) { select() }
                    .decodeList<UserPhotocardBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val photocardArtistUpd = supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST).upsert(backendPhotocardArtistCR) { select() }
                    .decodeList<PhotocardArtistCrossRefBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val photocardTagUpd = supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG).upsert(backendPhotocardTagCR) { select() }
                    .decodeList<PhotocardArtistCrossRefBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                database.withTransaction {
                    photocardDao.updateAll(localPackage.photocards.map { it.copy(isSynchronized = true, updatedAt = photocardsUpd) })
                    crossRefDao.updatePhotocardArtist(localPackage.photocardArtistCR.map { it.copy(isSynchronized = true, updatedAt = photocardArtistUpd) })
                    crossRefDao.updatePhotocardTag(localPackage.photocardTagCR.map { it.copy(isSynchronized = true, updatedAt = photocardTagUpd) })
                }
                Log.d(TAG, "Photocards were synchronized as planned, local flags updated")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to update photocards: ", e)
            }
        }
        if (backendTags.isNotEmpty() || backendPhotocardTagCR.isNotEmpty()) {
            try {
                val tagsUpd = supabase.from(BackendTables.TAGS).upsert(backendTags) { select() }
                    .decodeList<TagBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                database.withTransaction {
                    tagDao.updateAll(localPackage.tags.map { it.copy(isSynchronized = true, updatedAt = tagsUpd) })
                }
                Log.d(TAG, "Tags were synchronized as planned, local flags updated")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to update tags: ", e)
            }
        }
        Log.i(TAG, "Sync finished, remote tables were actualized")
    }

    private suspend fun downloadRemoteChanges(lastSyncTimestamp: String): Long? {
        // DOWNLOADING FROM REMOTE
        val backPackage = try {
            coroutineScope {
                val backendArtistSettings = supabase.from(BackendTables.USER.ARTIST_SETTINGS)
                    .select { filter { UserArtistSettingsBackend::updatedAt gt lastSyncTimestamp } }
                    .decodeList<UserArtistSettingsBackend>()
                val userArtistIds = backendArtistSettings.map { it.artistId }
                val backendArtists = supabase.from(BackendTables.GLOBAL.ARTISTS)
                    .select { filter { GlobalArtistBackend::artistId isIn userArtistIds } }
                    .decodeList<GlobalArtistBackend>()
                val backendArtistHierarchy = supabase.from(BackendTables.GLOBAL.ARTIST_HIERARCHY)
                    .select {
                        filter {
                            and {
                                GlobalArtistHierarchyBackend::groupId isIn userArtistIds
                                GlobalArtistHierarchyBackend::memberId isIn userArtistIds
                                GlobalArtistHierarchyBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeList<GlobalArtistHierarchyBackend>()
                val backendAlbums = supabase.from(BackendTables.USER.ALBUMS)
                    .select { filter { UserAlbumBackend::updatedAt gt lastSyncTimestamp } }
                    .decodeList<UserAlbumBackend>()
                val backendPhotocards = supabase.from(BackendTables.USER.PHOTOCARDS)
                    .select { filter { UserPhotocardBackend::updatedAt gt lastSyncTimestamp } }
                    .decodeList<UserPhotocardBackend>()
                val backendAACrossRef = supabase.from(BackendTables.CROSSREF.ALBUM_ARTIST)
                    .select { filter { AlbumArtistCrossRefBackend::updatedAt gt lastSyncTimestamp } }
                    .decodeList<AlbumArtistCrossRefBackend>()
                val backendPACrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST)
                    .select { filter { PhotocardArtistCrossRefBackend::updatedAt gt lastSyncTimestamp } }
                    .decodeList<PhotocardArtistCrossRefBackend>()
                val backendPTCrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG)
                    .select { filter { PhotocardTagCrossRefBackend::updatedAt gt lastSyncTimestamp } }
                    .decodeList<PhotocardTagCrossRefBackend>()
                val backendTags = supabase.from(BackendTables.TAGS)
                    .select { filter { TagBackend::updatedAt gt lastSyncTimestamp } }
                    .decodeList<TagBackend>()

                BackendDataPackage(backendArtists, backendArtistHierarchy, backendAlbums, backendArtistSettings, backendPhotocards, backendAACrossRef, backendPACrossRef, backendPTCrossRef, backendTags)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to download data from remote: ", e)
            return null
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

        // UPDATE THE LOCAL DATABASE
        database.withTransaction {
            try {
                artistDao.upsertAll(localArtists)
                tagDao.upsertAll(localTags)
                albumDao.upsertAll(localAlbums)
                photocardDao.upsertAll(localPhotocards)
                artistDao.upsertGroupLinks(localArtistArtistCF)
                albumDao.upsertArtistLinks(localAlbumArtistCF)
                photocardDao.upsertArtistLinks(localPhotocardArtistCF)
                tagDao.upsertTagLinks(localPhotocardTagCF)
                Log.d(TAG, "Download from remote completed")

            } catch (e: Exception) {
                Log.d(TAG, "Failed to save remote data: ", e)
                return@withTransaction null
            }
        }

        val allTimes = mutableListOf<Long>()
        with(allTimes) {
            addAll(localArtists.map { it.updatedAt })
            addAll(localAlbums.map { it.updatedAt })
            addAll(localTags.map { it.updatedAt })
            addAll(localPhotocards.map { it.updatedAt })
            addAll(localArtistArtistCF.map { it.updatedAt })
            addAll(localAlbumArtistCF.map { it.updatedAt })
            addAll(localPhotocardArtistCF.map { it.updatedAt })
            addAll(localPhotocardTagCF.map { it.updatedAt })
        }
        val syncTimestamp = allTimes.maxOrNull()
        return syncTimestamp
    }

    private suspend fun clearDeleted() {
        database.withTransaction {
            try {
                photocardDao.clearDeleted()
                albumDao.clearDeleted()
                tagDao.clearDeleted()
                artistDao.clearDeleted()
                Log.d(TAG, "Cleared all entities marked as deleted")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to clear deleted data: ", e)
            }
        }
    }
}

    @Serializable
    data class CountResponse(val count: Long)

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

private fun now(): Long {
    return System.currentTimeMillis()
}

private fun nowStr(): String {
    return now().toTimestamptz()
}