package com.vbshkn.ikollect.data.background

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.vbshkn.ikollect.data.local.dao.AlbumDao
import com.vbshkn.ikollect.data.local.dao.ArtistDao
import com.vbshkn.ikollect.data.local.dao.CrossRefDao
import com.vbshkn.ikollect.data.local.dao.PhotocardDao
import com.vbshkn.ikollect.data.local.dao.TagDao
import com.vbshkn.ikollect.data.local.database.AppDatabase
import com.vbshkn.ikollect.data.local.datastore.LocalSettings
import com.vbshkn.ikollect.data.local.datastore.LocalSettingsStorage
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
import com.vbshkn.ikollect.data.remote.backend.model.UserSettingsBackend
import com.vbshkn.ikollect.di.ApplicationScope
import com.vbshkn.ikollect.util.now
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.math.max
import kotlin.time.Instant
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Singleton

private const val TAG = "SyncManager"

@Singleton
class SyncManager @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val supabase: SupabaseClient,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val photocardDao: PhotocardDao,
    private val tagDao: TagDao,
    private val crossRefDao: CrossRefDao,
    private val backendStorageDao: BackendStorageDao,
    private val serviceLogStorage: ServiceLogStorage,
    private val settingsStorage: LocalSettingsStorage
) {
    companion object {
        private val handshakeMutex = Mutex()
    }

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
                val backendArtistSettings =
                    supabase.from(BackendTables.USER.ARTIST_SETTINGS).select {
                        filter { UserArtistSettingsBackend::isDeleted eq false }
                    }.decodeList<UserArtistSettingsBackend>()
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
                val backendAlbums = supabase.from(BackendTables.USER.ALBUMS).select {
                    filter { UserAlbumBackend::isDeleted eq false }
                }.decodeList<UserAlbumBackend>()
                val backendPhotocards = supabase.from(BackendTables.USER.PHOTOCARDS).select {
                    filter { UserPhotocardBackend::isDeleted eq false }
                }.decodeList<UserPhotocardBackend>()
                val backendAACrossRef = supabase.from(BackendTables.CROSSREF.ALBUM_ARTIST).select {
                    filter { AlbumArtistCrossRefBackend::isDeleted eq false }
                }.decodeList<AlbumArtistCrossRefBackend>()
                val backendPACrossRef =
                    supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST).select {
                        filter { PhotocardArtistCrossRefBackend::isDeleted eq false }
                    }.decodeList<PhotocardArtistCrossRefBackend>()
                val backendPTCrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG).select {
                    filter { PhotocardTagCrossRefBackend::isDeleted eq false }
                }.decodeList<PhotocardTagCrossRefBackend>()
                val backendTags = supabase.from(BackendTables.TAGS).select {
                    filter { TagBackend::isDeleted eq false }
                }.decodeList<TagBackend>()
                val backendSettings = supabase.from(BackendTables.USER.SETTINGS).select {
                    filter { UserSettingsBackend::isDeleted eq false }
                }.decodeSingle<UserSettingsBackend>()

                BackendDataPackage(
                    backendArtists,
                    backendArtistHierarchy,
                    backendAlbums,
                    backendArtistSettings,
                    backendPhotocards,
                    backendAACrossRef,
                    backendPACrossRef,
                    backendPTCrossRef,
                    backendTags,
                    backendSettings
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
        val localSettings = backPackage.settings.settings

        try {
            database.withTransaction {
                // CLEANING THE LOCAL DB
                clearLocalDatabase()

                // INSERT TO THE LOCAL DATABASE
                artistDao.insertAll(localArtists)
                tagDao.insertAll(localTags)
                albumDao.insertAll(localAlbums)
                photocardDao.insertAll(localPhotocards)

                artistDao.insertGroupLinks(localArtistArtistCF)
                albumDao.upsertArtistLinks(localAlbumArtistCF)
                photocardDao.insertArtistLinks(localPhotocardArtistCF)
                tagDao.upsertTagLinks(localPhotocardTagCF)
                Log.d(TAG, "Download from remote completed")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to clear the database: ", e)
        }
        localSettings?.let { settingsStorage.update(it) }
    }

    private fun clearLocalDatabase() {
        try {
            database.clearAllTables()
            Log.i(TAG, "Local database cleared.")
        } catch (e: Exception) {
            Log.d(TAG, "Failed to clear local database: ", e)
        }
    }

    private suspend fun uploadAllToBackend(userId: String) {
        // LOADING LOCAL DATA
        val localPackage = try {
            val localAlbumArtist = crossRefDao.getAlbumArtist()
            val localArtistArtist = crossRefDao.getArtistArtist()
            val localPhotocardArtist = crossRefDao.getPhotocardArtist()
            val localPhotocardTag = crossRefDao.getPhotocardTag()
            val localAlbums = albumDao.getAllShot()
            val localArtists = artistDao.getAllShot()
            val localPhotocards = photocardDao.getAllShot()
            val localTags = tagDao.getAllShot()
            val localSettings = settingsStorage.getCurrentSettings()

            LocalDataPackage(
                localAlbumArtist,
                localArtistArtist,
                localPhotocardArtist,
                localPhotocardTag,
                localAlbums,
                localArtists,
                localPhotocards,
                localTags,
                localSettings
            )
        } catch (e: Exception) {
            Log.d(TAG, "Failed to download local data: ", e)
            return
        }

        // COPYING LOCAL IMAGES
        val modifiedPackage = localPackage.copy(
            albums = localPackage.albums.map {
                val uri = it.imageUrl?.toUri()
                if (uri?.scheme == "file://") {
                    val newPath = backendStorageDao.upsertAlbumImage(uri, it.albumId)
                    val copy = it.copy(imageUrl = newPath ?: it.imageUrl)
                    albumDao.updateAlbum(copy)
                    copy
                } else {
                    it
                }
            },
            photocards = localPackage.photocards.map {
                val uri = it.imageUrl?.toUri()
                if (uri?.scheme == "file://") {
                    val newPath = backendStorageDao.upsertPhotocardImage(uri, it.photocardId)
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
        val backendPhotocardArtistCR =
            modifiedPackage.photocardArtistCR.map { it.toBackend(userId) }
        val backendPhotocardTagCR = modifiedPackage.photocardTagCR.map { it.toBackend(userId) }
        val backendArtistHierarchy = modifiedPackage.artistArtistCR.map { it.toBackend() }
        val backendArtist = modifiedPackage.artists.map { it.toBackend() }
        val backendArtistSettings = modifiedPackage.artists.map { it.toBackend(userId) }
        val backendAlbums = modifiedPackage.albums.map { it.toBackend(userId) }
        val backendPhotocards = modifiedPackage.photocards.map { it.toBackend(userId) }
        val backendTags =
            modifiedPackage.tags.filter { !it.isSystemTag }.map { it.toBackend(userId) }
        val backendSettings = modifiedPackage.settings.toBackend(userId)

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
                supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST)
                    .insert(backendPhotocardArtistCR)
            }
            if (backendPhotocardTagCR.isNotEmpty()) {
                supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG).insert(backendPhotocardTagCR)
            }
            if (backendArtistHierarchy.isNotEmpty()) {
                supabase.from(BackendTables.GLOBAL.ARTIST_HIERARCHY).upsert(backendArtistHierarchy)
            }
            supabase.from(BackendTables.USER.SETTINGS).upsert(backendSettings)
            Log.d(TAG, "Upload completed successfully")
        } catch (e: Exception) {
            Log.d(TAG, "Upload failed: ", e)
        }
    }

    suspend fun offlineCheckup() {
        Log.i(TAG, "Starting offline checkup...")
        val startPoint = now()

        Log.i(TAG, "Step 1: Looking for unused artists to delete...")
        softDeleteUnusedArtists()
        Log.i(TAG, "Step 2: Clear Deleted")
        clearDeleted()

        val finishPoint = now()
        val totalSeconds = (finishPoint - startPoint) / 1000f
        Log.i(TAG, "Offline checkup finished in $totalSeconds seconds.")
    }

    // ================================ HANDSHAKE =================================================

    suspend fun performHandshake(userId: String): HandshakeResult {
        if (handshakeMutex.isLocked) {
            Log.d(TAG, "Sync is already in process. Skipping...")
            return HandshakeResult.Skip
        }

        handshakeMutex.withLock {
            Log.i(TAG, "Starting scheduled handshake...")
            val startPoint = now()
            val lastSyncTimestamp = serviceLogStorage.getLastSyncTimestamp()
                ?: Instant.fromEpochMilliseconds(0).toString()
            Log.d(TAG, "Last sync at $lastSyncTimestamp")

            Log.i(TAG, "Step 0: Looking for unused artists to delete...")
            val zeroStepSucceed = softDeleteUnusedArtists()
            Log.i(TAG, "Step 1: Local -> Remote")
            val firstStepSucceed = uploadLocalChanges(userId)
            Log.i(TAG, "Step 2: Local <- Remote")
            val (secondStepSucceed, finishTimestamp) = downloadRemoteChanges(lastSyncTimestamp)
            Log.i(TAG, "Step 3: Clear Deleted")
            val thirdStepSucceed = clearDeleted()

            // Updating last sync timestamp
            // Decreasing it a lil bit just to avoid blind zone
            val finishPoint = now()
            val totalSeconds = (finishPoint - startPoint) / 1000f
            finishTimestamp?.let {
                serviceLogStorage.updateLastSyncTimestamp(finishTimestamp.toTimestamptz())
                Log.i(
                    TAG,
                    "Handshake finished at ${finishTimestamp.toTimestamptz()}\nTook $totalSeconds seconds to perform"
                )
            }

            val results =
                listOf(zeroStepSucceed, firstStepSucceed, secondStepSucceed, thirdStepSucceed)
            val result = if (results.all { it is HandshakeResult.FullSuccess }) {
                HandshakeResult.FullSuccess
            } else if (results.all { it is HandshakeResult.Fail }) {
                HandshakeResult.Fail
            } else {
                HandshakeResult.PartialSuccess
            }
            Log.i(TAG, "Result: ${result.javaClass.simpleName}")
            return result
        }
    }

    private suspend fun softDeleteUnusedArtists(): HandshakeResult {
        return try {
            artistDao.softDeleteUnusedGroups()
            artistDao.softDeleteUnusedSoloists()
            HandshakeResult.FullSuccess
        } catch (e: Exception) {
            Log.d(TAG, "Failed to soft delete unused local artists: ", e)
            HandshakeResult.Fail
        }
    }

    private suspend fun uploadLocalChanges(userId: String): HandshakeResult {
        var result: HandshakeResult = HandshakeResult.FullSuccess
        val localPackage = try {
            database.withTransaction {
                val localAlbumArtist = crossRefDao.getAlbumArtistUnSynchronized()
                val localArtistArtist = crossRefDao.getArtistArtistUnSynchronized()
                val localPhotocardArtist = crossRefDao.getPhotocardArtistUnSynchronized()
                val localPhotocardTag = crossRefDao.getPhotocardTagUnSynchronized()
                val localAlbums = albumDao.getUnSynchronizedShot()
                val localArtists = artistDao.getUnSynchronizedShot()
                val localPhotocards = photocardDao.getUnSynchronizedShot()
                val localTags = tagDao.getUnSynchronizedShot()
                val localSettings = settingsStorage.getCurrentSettings()

                LocalDataPackage(
                    localAlbumArtist,
                    localArtistArtist,
                    localPhotocardArtist,
                    localPhotocardTag,
                    localAlbums,
                    localArtists,
                    localPhotocards,
                    localTags,
                    localSettings
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to download local data: ", e)
            result = HandshakeResult.Fail
            return result
        }

        // COPYING LOCAL IMAGES
        val modifiedPackage = localPackage.copy(
            albums = localPackage.albums.map {
                val uri = it.imageUrl?.toUri()
                if (uri?.scheme == "file") {
                    val newPath = backendStorageDao.upsertAlbumImage(uri, it.albumId)
                    val copy = it.copy(imageUrl = newPath ?: it.imageUrl)
                    albumDao.updateAlbum(copy)
                    copy
                } else {
                    it
                }
            },
            photocards = localPackage.photocards.map {
                val uri = it.imageUrl?.toUri()
                if (uri?.scheme == "file") {
                    val newPath = backendStorageDao.upsertPhotocardImage(uri, it.photocardId)
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
        val backendPhotocardArtistCR =
            modifiedPackage.photocardArtistCR.map { it.toBackend(userId) }
        val backendPhotocardTagCR = modifiedPackage.photocardTagCR.map { it.toBackend(userId) }
        val backendArtistHierarchy = modifiedPackage.artistArtistCR.map { it.toBackend() }
        val backendArtist = modifiedPackage.artists.map { it.toBackend() }
        val backendArtistSettings = modifiedPackage.artists.map { it.toBackend(userId) }
        val backendAlbums = modifiedPackage.albums.map { it.toBackend(userId) }
        val backendPhotocards = modifiedPackage.photocards.map { it.toBackend(userId) }
        val backendTags = modifiedPackage.tags.map { it.toBackend(userId) }
        val backendSettings = modifiedPackage.settings.toBackend(userId)

        // SYNCHRONIZING
        if (backendArtist.isNotEmpty() || backendArtistSettings.isNotEmpty() || backendArtistHierarchy.isNotEmpty()) {
            try {
                val artistsUpd =
                    supabase.from(BackendTables.GLOBAL.ARTISTS).upsert(backendArtist) { select() }
                        .decodeList<GlobalArtistBackend>()
                        .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val artistSettingsUpd = supabase.from(BackendTables.USER.ARTIST_SETTINGS)
                    .upsert(backendArtistSettings) { select() }
                    .decodeList<UserArtistSettingsBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val hierarchyUpd = supabase.from(BackendTables.GLOBAL.ARTIST_HIERARCHY)
                    .upsert(backendArtistHierarchy) { select() }
                    .decodeList<GlobalArtistHierarchyBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                database.withTransaction {
                    artistDao.updateAll(localPackage.artists.map {
                        it.copy(
                            isSynchronized = true,
                            updatedAt = max(artistsUpd, artistSettingsUpd)
                        )
                    })
                    crossRefDao.updateArtistArtist(localPackage.artistArtistCR.map {
                        it.copy(
                            isSynchronized = true,
                            updatedAt = hierarchyUpd
                        )
                    })
                }
                Log.d(TAG, "Artists were synchronized as planned, local flags updated")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to update artists: ", e)
                result = HandshakeResult.PartialSuccess
            }
        }
        if (backendAlbums.isNotEmpty() || backendAlbumArtistCR.isNotEmpty()) {
            try {
                val albumsUpd =
                    supabase.from(BackendTables.USER.ALBUMS).upsert(backendAlbums) { select() }
                        .decodeList<UserAlbumBackend>()
                        .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val albumArtistUpd = supabase.from(BackendTables.CROSSREF.ALBUM_ARTIST)
                    .upsert(backendAlbumArtistCR) { select() }
                    .decodeList<AlbumArtistCrossRefBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                database.withTransaction {
                    albumDao.updateAll(modifiedPackage.albums.map {
                        it.copy(
                            isSynchronized = true,
                            updatedAt = albumsUpd
                        )
                    })
                    crossRefDao.updateAlbumArtist(localPackage.albumArtistCR.map {
                        it.copy(
                            isSynchronized = true,
                            updatedAt = albumArtistUpd
                        )
                    })
                }
                Log.d(TAG, "Albums were synchronized as planned, local flags updated")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to update albums: ", e)
                result = HandshakeResult.PartialSuccess
            }
        }
        if (backendPhotocards.isNotEmpty() || backendPhotocardArtistCR.isNotEmpty()) {
            try {
                val photocardsUpd = supabase.from(BackendTables.USER.PHOTOCARDS)
                    .upsert(backendPhotocards) { select() }
                    .decodeList<UserPhotocardBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val photocardArtistUpd = supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST)
                    .upsert(backendPhotocardArtistCR) { select() }
                    .decodeList<PhotocardArtistCrossRefBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                database.withTransaction {
                    photocardDao.updateAll(localPackage.photocards.map {
                        it.copy(
                            isSynchronized = true,
                            updatedAt = photocardsUpd
                        )
                    })
                    crossRefDao.updatePhotocardArtist(localPackage.photocardArtistCR.map {
                        it.copy(
                            isSynchronized = true,
                            updatedAt = photocardArtistUpd
                        )
                    })
                }
                Log.d(TAG, "Photocards were synchronized as planned, local flags updated")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to update photocards: ", e)
                result = HandshakeResult.PartialSuccess
            }
        }
        if (backendTags.isNotEmpty() || backendPhotocardTagCR.isNotEmpty()) {
            try {
                val tagsUpd = supabase.from(BackendTables.TAGS)
                    .upsert(backendTags) { select() }
                    .decodeList<TagBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                val photocardTagUpd = supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG)
                    .upsert(backendPhotocardTagCR) { select() }
                    .decodeList<PhotocardTagCrossRefBackend>()
                    .maxOfOrNull { it.updatedAt.toTimeMillis() } ?: now()
                database.withTransaction {
                    tagDao.updateAll(localPackage.tags.map {
                        it.copy(
                            isSynchronized = true,
                            updatedAt = tagsUpd
                        )
                    })
                }
                crossRefDao.updatePhotocardTag(localPackage.photocardTagCR.map {
                    it.copy(
                        isSynchronized = true,
                        updatedAt = photocardTagUpd
                    )
                })
                Log.d(TAG, "Tags were synchronized as planned, local flags updated")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to update tags: ", e)
                result = HandshakeResult.PartialSuccess
            }
        }
        try {
            supabase.from(BackendTables.USER.SETTINGS).upsert(backendSettings)
            Log.d(TAG, "App settings were synchronized as planned, local flags updated")
        } catch (e: Exception) {
            Log.d(TAG, "Failed to update app settings: ", e)
            result = HandshakeResult.PartialSuccess
        }
        Log.i(TAG, "Sync finished, remote tables were actualized")
        return result
    }

    private suspend fun downloadRemoteChanges(lastSyncTimestamp: String): Pair<HandshakeResult, Long?> {
        var result: HandshakeResult = HandshakeResult.FullSuccess
        // DOWNLOADING FROM REMOTE
        val backPackage = try {
            coroutineScope {
                val backendArtistSettings = supabase.from(BackendTables.USER.ARTIST_SETTINGS)
                    .select {
                        filter {
                            and {
                                UserArtistSettingsBackend::isDeleted eq false
                                UserArtistSettingsBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeList<UserArtistSettingsBackend>()
                val userArtistIds = backendArtistSettings.map { it.artistId }
                val backendArtists = supabase.from(BackendTables.GLOBAL.ARTISTS)
                    .select {
                        filter {
                            GlobalArtistBackend::artistId isIn userArtistIds
                        }
                    }
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
                    .select {
                        filter {
                            and {
                                UserAlbumBackend::isDeleted eq false
                                UserAlbumBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeList<UserAlbumBackend>()
                val backendPhotocards = supabase.from(BackendTables.USER.PHOTOCARDS)
                    .select {
                        filter {
                            and {
                                UserPhotocardBackend::isDeleted eq false
                                UserPhotocardBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeList<UserPhotocardBackend>()
                val backendAACrossRef = supabase.from(BackendTables.CROSSREF.ALBUM_ARTIST)
                    .select {
                        filter {
                            and {
                                AlbumArtistCrossRefBackend::isDeleted eq false
                                AlbumArtistCrossRefBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeList<AlbumArtistCrossRefBackend>()
                val backendPACrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_ARTIST)
                    .select {
                        filter {
                            and {
                                PhotocardArtistCrossRefBackend::isDeleted eq false
                                PhotocardArtistCrossRefBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeList<PhotocardArtistCrossRefBackend>()
                val backendPTCrossRef = supabase.from(BackendTables.CROSSREF.PHOTOCARD_TAG)
                    .select {
                        filter {
                            and {
                                PhotocardTagCrossRefBackend::isDeleted eq false
                                PhotocardTagCrossRefBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeList<PhotocardTagCrossRefBackend>()
                val backendTags = supabase.from(BackendTables.TAGS)
                    .select {
                        filter {
                            and {
                                TagBackend::isDeleted eq false
                                TagBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeList<TagBackend>()
                val backendSettings = supabase.from(BackendTables.USER.SETTINGS)
                    .select {
                        filter {
                            and {
                                UserSettingsBackend::isDeleted eq false
                                UserSettingsBackend::updatedAt gt lastSyncTimestamp
                            }
                        }
                    }
                    .decodeSingle<UserSettingsBackend>()

                BackendDataPackage(
                    backendArtists,
                    backendArtistHierarchy,
                    backendAlbums,
                    backendArtistSettings,
                    backendPhotocards,
                    backendAACrossRef,
                    backendPACrossRef,
                    backendPTCrossRef,
                    backendTags,
                    backendSettings
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to download data from remote: ", e)
            return HandshakeResult.Fail to null
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
        val localSettings = backPackage.settings.settings

        // UPDATE THE LOCAL DATABASE
        try {
            database.withTransaction {
                artistDao.upsertAll(localArtists)
                tagDao.upsertAll(localTags)
                albumDao.upsertAll(localAlbums)
                photocardDao.upsertAll(localPhotocards)
                artistDao.insertGroupLinks(localArtistArtistCF)
                albumDao.upsertArtistLinks(localAlbumArtistCF)
                photocardDao.insertArtistLinks(localPhotocardArtistCF)
                tagDao.upsertTagLinks(localPhotocardTagCF)
                Log.d(TAG, "Download from remote completed")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Failed to save remote data: ", e)
            result = HandshakeResult.Fail
        }
        localSettings?.let { settingsStorage.update(it) }

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
        return result to syncTimestamp
    }

    private suspend fun clearDeleted(): HandshakeResult {
        var result: HandshakeResult = HandshakeResult.FullSuccess
        database.withTransaction {
            try {
                photocardDao.clearDeleted()
                albumDao.clearDeleted()
                tagDao.clearDeleted()
                artistDao.clearDeleted()
                Log.d(TAG, "Cleared all entities marked as deleted")
            } catch (e: Exception) {
                Log.d(TAG, "Failed to clear deleted data: ", e)
                result = HandshakeResult.Fail
            }
        }
        return result
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
    val tags: List<TagBackend>,
    val settings: UserSettingsBackend
)

data class LocalDataPackage(
    val albumArtistCR: List<AlbumArtistCrossRef>,
    val artistArtistCR: List<ArtistArtistCrossRef>,
    val photocardArtistCR: List<PhotocardArtistCrossRef>,
    val photocardTagCR: List<PhotocardTagCrossRef>,
    val albums: List<AlbumEntity>,
    val artists: List<ArtistEntity>,
    val photocards: List<PhotocardEntity>,
    val tags: List<TagEntity>,
    val settings: LocalSettings
)

sealed interface HandshakeResult {
    object FullSuccess : HandshakeResult
    object PartialSuccess : HandshakeResult
    object Fail : HandshakeResult
    object Skip : HandshakeResult
}