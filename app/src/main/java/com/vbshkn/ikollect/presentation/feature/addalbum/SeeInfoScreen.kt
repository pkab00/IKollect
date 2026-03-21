package com.vbshkn.ikollect.presentation.feature.addalbum

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.util.UiText

@Composable
fun SeeInfoScreen(
    viewModel: AddAlbumViewModel,
    paddingValues: PaddingValues,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val candidate = uiState.albumCandidate

    BackHandler(enabled = true) {
        viewModel.onEvent(AddAlbumContract.Event.OnExitClicked)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(top = 16.dp)
    ) {
        with(candidate) {
            item { DataItem(
                label = UiText.StringResource(R.string.see_info_name),
                data = name
            ) }
            item { DataItem(
                label = UiText.StringResource(R.string.see_info_artist),
                data = artists.joinToString { it.name }
            ) }
            item { DataItem(
                label = UiText.StringResource(R.string.see_info_release_year),
                data = releaseDate
            ) }
            item { DataItem(
                label = UiText.StringResource(R.string.see_info_barcode),
                data = barcodeNumber
            ) }
            item { DataItem(
                label = UiText.StringResource(R.string.see_info_discogs_id),
                data = albumId.toString()
            ) }
        }
    }
}

@Composable
private fun DataItem(
    label: UiText,
    data: String
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = label.asString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = data,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}