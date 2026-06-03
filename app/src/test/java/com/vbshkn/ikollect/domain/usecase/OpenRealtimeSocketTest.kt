package com.vbshkn.ikollect.domain.usecase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.realtime
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OpenRealtimeSocketTest {
    private val supabase = mockk<SupabaseClient>(relaxed = true)
    private val scope = TestScope()
    private val openRealtimeSocket = OpenRealtimeSocket(supabase, scope)

    @BeforeEach
    fun setUp() {
        mockSupabaseModules(supabase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `invoke should open realtime socket via supabase`() = runTest {
        // Given
        coEvery { supabase.realtime.connect() } just Runs

        // When
        openRealtimeSocket()
        scope.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { supabase.realtime.connect() }
    }
}