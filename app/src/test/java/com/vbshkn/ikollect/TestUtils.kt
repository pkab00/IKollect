package com.vbshkn.ikollect

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
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
    every { client.realtime } returns mockk<Realtime>(relaxed = true)

    mockkStatic("io.github.jan.supabase.auth.AuthKt")
    every { client.auth } returns mockk<Auth>(relaxed = true)

    mockkStatic("io.github.jan.supabase.postgrest.PostgrestKt")
    every { client.postgrest } returns mockk<Postgrest>(relaxed = true)
}