package com.vbshkn.ikollect.presentation.feature.albums.wizard.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.candidate.VersionCandidate
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardContract
import com.vbshkn.ikollect.presentation.feature.albums.wizard.AlbumWizardViewModel

@Composable
fun SelectVersionScreen(viewModel: AlbumWizardViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val versions = uiState.albumCandidate!!.versionCandidates
    val selectedVersion = uiState.versionCandidate
    val blankVersionCandidate = VersionCandidate("", null)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        items(versions + blankVersionCandidate) { version ->
            VersionPreview(
                version = version,
                selectedVersion = selectedVersion,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
private fun VersionPreview(
    version: VersionCandidate,
    selectedVersion: VersionCandidate?,
    onEvent: (AlbumWizardContract.Event) -> Unit
) {
    OutlinedCard(
        onClick = { onEvent(AlbumWizardContract.Event.OnUpdateVersion(version)) },
        border = CardDefaults.outlinedCardBorder(enabled = version == selectedVersion),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (version == selectedVersion)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else Color.Transparent
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            VersionImage(version.coverImage)
            Text(
                text = version.name.ifBlank { stringResource(R.string.wizard_cant_find_version) },
                textAlign = TextAlign.Center,
                maxLines = 3,
                modifier = Modifier
                    .padding(8.dp)
                    .heightIn(min = 50.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
private fun VersionImage(imageUrl: String?) {
    if (imageUrl == null) {
        Box(
            Modifier
                .width(100.dp)
                .height(135.dp)
                .background(Color.LightGray)
        )
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .height(135.dp)
        )
    }
}