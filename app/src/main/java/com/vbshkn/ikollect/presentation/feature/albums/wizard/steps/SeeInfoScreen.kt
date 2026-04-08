package com.vbshkn.ikollect.presentation.feature.albums.wizard.steps

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.DataOutlinedCard
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardContract
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel
import com.vbshkn.ikollect.util.UiText

@Composable
fun SeeInfoScreen(viewModel: AlbumWizardViewModel, ) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val candidate = uiState.albumCandidate

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        candidate?.let {
            item { DataOutlinedCard(
                label = UiText.StringResource(R.string.see_info_name),
                data = UiText.DynamicString(it.name)
            ) }
            item { DataOutlinedCard(
                label = UiText.StringResource(R.string.see_info_artist),
                data = UiText.DynamicString(it.artistCandidates.joinToString { it.name })
            ) }
            item { DataOutlinedCard(
                label = UiText.StringResource(R.string.see_info_release_year),
                data = UiText.DynamicString(it.releaseDate)
            ) }
            item { DataOutlinedCard(
                label = UiText.StringResource(R.string.see_info_barcode),
                data = UiText.DynamicString(it.barcodeNumber)
            ) }
            item { DataOutlinedCard(
                label = UiText.StringResource(R.string.see_info_discogs_id),
                data = UiText.DynamicString(it.discogsAlbumId.toString())
            ) }
        }
    }
}