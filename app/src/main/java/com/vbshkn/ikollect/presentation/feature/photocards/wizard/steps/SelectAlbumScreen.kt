package com.vbshkn.ikollect.presentation.feature.photocards.wizard.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.AlbumOverview
import com.vbshkn.ikollect.domain.model.ArtistOverview
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardContract
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.util.UiText

@Composable
fun SelectAlbumScreen(viewModel: PhotocardWizardViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            WizardItemWrapper(
                title = UiText.StringResource(R.string.photocard_wizard_subtitle_select_album),
                content = {}
            )
        }
        items(
            items = uiState.albumOverviews,
            key = { it.albumId }
        ) { album ->
            SelectableAlbum(
                album = album,
                selectedAlbumId = uiState.photocardCandidate.albumId,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
private fun SelectableAlbum(
    album: AlbumOverview,
    selectedAlbumId: Long?,
    onEvent: (PhotocardWizardContract.Event) -> Unit
) {
    OutlinedCard(
        onClick = { onEvent(PhotocardWizardContract.Event.OnAlbumSelected(album.albumId)) },
        border = CardDefaults.outlinedCardBorder(enabled = album.albumId == selectedAlbumId),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (album.albumId == selectedAlbumId)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else Color.Transparent
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = album.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(150.dp)
            )
            Text(
                text = album.extendedName,
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(8.dp)
                    .heightIn(min = 30.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
            )
            if (album.komcaNumber != null) {
                Text(
                    text = "KOMCA: ${album.komcaNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(8.dp)
                        .heightIn(min = 16.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .heightIn(min = 16.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
        }
    }
}