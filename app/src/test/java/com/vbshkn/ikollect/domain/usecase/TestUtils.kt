package com.vbshkn.ikollect.domain.usecase

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic

fun mockRoomTransactions(database: RoomDatabase) {
    mockkStatic("androidx.room.RoomDatabaseKt")
    coEvery { database.withTransaction(any<suspend () -> Any>()) } coAnswers {
        val block = secondArg<suspend () -> Any>()
        block()
    }
}

fun mockSupabaseModules(client: SupabaseClient) {
    mockkStatic("io.github.jan.supabase.realtime.RealtimeKt")
    every { client.realtime } returns mockk<Realtime>()
}