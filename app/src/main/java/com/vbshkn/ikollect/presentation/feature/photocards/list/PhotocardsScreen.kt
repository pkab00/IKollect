package com.vbshkn.ikollect.presentation.feature.photocards.list

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.presentation.composable.SmallTextLabel
import com.vbshkn.ikollect.presentation.composable.TagLabel
import com.vbshkn.ikollect.util.PaletteUtil
import com.vbshkn.ikollect.util.UiText
import kotlinx.coroutines.flow.Flow

@Composable
fun PhotocardsScreen(
    viewModel: PhotocardsViewModel,
    onGoToWizard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PhotocardsContract.Effect.GoToWizard -> onGoToWizard()
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(onEvent = viewModel::omEvent)
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp)
        ) {
            if (uiState.error != null) {
                Text(
                    text = stringResource(R.string.error_loading_photocards),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            else if (uiState.photocards.isEmpty()) {
                Text(
                    text = stringResource(R.string.filler_nothing_to_show),
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.photocards,
                        key = { it.photocardId }
                    ) { photocard ->
                        PhotocardCard(
                            photocard = photocard,
                            onEvent = viewModel::omEvent
                        )
                    }
                }
            }
        }
    }
    ImageZoomOverlay(
        enable = uiState.fullScreenPreview != null,
        contentUrl = uiState.fullScreenPreview
    )
}

@Composable
fun PhotocardCard(
    photocard: PhotocardListItem,
    onEvent: (PhotocardsContract.Event) -> Unit
) {
    Card(
        onClick = {},
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.heightIn(250.dp)
    ) {
        val haptic = LocalHapticFeedback.current

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(
                        colors = buildList {
                            for (v in 1..10) {
                                add(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = v/10f))
                            }
                        }
                    ))
                    .pointerInput(Unit) {
                        detectTapGestures (
                            onLongPress = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onEvent(PhotocardsContract.Event.OnPhotocardPreviewPressed(photocard.imageUrl))
                            },
                            onPress = {
                                val release = tryAwaitRelease()
                                if (release) {
                                    onEvent(PhotocardsContract.Event.OnPhotocardPreviewReleased)
                                }
                            }
                        )
                    }
            ) {
                AsyncImage(
                    model = photocard.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.59f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = photocard.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.height(46.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = photocard.owner.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    maxItemsInEachRow = 2,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    photocard.tags.forEach { tagItem ->
                        TagLabel(
                            tag = tagItem,
                            isSelected = false,
                            modifier = Modifier.size(height = 24.dp, width = 75.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onEvent: (PhotocardsContract.Event) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.screen_title_photocards),
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(
                onClick = { onEvent(PhotocardsContract.Event.OnWizardClicked) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = ""
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}