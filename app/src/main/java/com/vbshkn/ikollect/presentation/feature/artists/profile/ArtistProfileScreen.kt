package com.vbshkn.ikollect.presentation.feature.artists.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay

@Composable
fun ArtistProfileScreen(
    viewModel: ArtistProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingOverlay()
    }
}