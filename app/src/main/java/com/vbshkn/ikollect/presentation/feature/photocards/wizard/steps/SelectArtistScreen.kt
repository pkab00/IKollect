package com.vbshkn.ikollect.presentation.feature.photocards.wizard.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardContract
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardViewModel
import com.vbshkn.ikollect.presentation.feature.wizard.WizardItemWrapper
import com.vbshkn.ikollect.util.UiText

@Composable
fun SelectArtistScreen(viewModel: PhotocardWizardViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            WizardItemWrapper(
                title = UiText.StringResource(R.string.photocard_wizard_subtitle_select_photo),
                showHint = true,
                onHint = { viewModel.onEvent(PhotocardWizardContract.Event.OnShowSelectArtistTip) },
                content = {}
            )
        }
        items(
            items = uiState.artists,
            key = { it.artistId }
        ) { artist ->
            SelectableAlbum(
                artist = artist,
                selectedArtistId = uiState.photocardCandidate.ownerId,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
private fun SelectableAlbum(
    artist: ArtistListItem,
    selectedArtistId: Long?,
    onEvent: (PhotocardWizardContract.Event) -> Unit
) {
    OutlinedCard(
        onClick = { onEvent(PhotocardWizardContract.Event.OnOwnerSelected(artist.artistId, artist.isGroup)) },
        border = CardDefaults.outlinedCardBorder(enabled = artist.artistId == selectedArtistId),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (artist.artistId == selectedArtistId)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else Color.Transparent
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = artist.profileImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(6.dp)
                    .clip(CircleShape)
            )
            Text(
                text = artist.name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(8.dp)
                    .heightIn(min = 30.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
            )
        }
    }
}