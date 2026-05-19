package com.vbshkn.ikollect.presentation

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.vbshkn.ikollect.data.background.BackgroundSyncWorker
import com.vbshkn.ikollect.data.repository.SettingsRepository
import com.vbshkn.ikollect.domain.usecase.OpenRealtimeSocket
import com.vbshkn.ikollect.presentation.navigation.NavBarDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val openRealtimeSocket: OpenRealtimeSocket,
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    init {
        openRealtimeSocket()
        collectSettings()
    }

    val settings = settingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun setUpBackgroundSync() = viewModelScope.launch {
        supabase.auth.awaitInitialization()

        val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
        val syncData = workDataOf("USER_ID" to userId)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val syncRequest = PeriodicWorkRequestBuilder<BackgroundSyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setInputData(syncData)
            .setConstraints(constraints)
            .build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                uniqueWorkName = "BackgroundSync",
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.REPLACE,
                request = syncRequest
            )
        Log.d(TAG, "Periodic sync scheduled for user: $userId")
    }

    fun collectSettings() = viewModelScope.launch {
        settingsRepository.getSettings().collect { settings ->
            Log.d(
                TAG, "Settings consumed:\n" +
                    "Theme: ${settings.theme}\n" +
                    "Language: ${settings.language}\n" +
                    "Navbar tabs order: ${settings.navBarDestinations}"
            )
            AppCompatDelegate.setDefaultNightMode(settings.theme)
            val locale = LocaleListCompat.forLanguageTags(settings.language)
            AppCompatDelegate.setApplicationLocales(locale)
        }
    }
}