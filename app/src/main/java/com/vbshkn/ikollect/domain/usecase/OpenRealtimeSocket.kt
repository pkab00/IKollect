package com.vbshkn.ikollect.domain.usecase

import com.vbshkn.ikollect.di.ApplicationScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class OpenRealtimeSocket @Inject constructor(
    private val supabase: SupabaseClient,
    @ApplicationScope private val scope: CoroutineScope
){
    operator fun invoke() = scope.launch {
        supabase.realtime.connect()
    }
}