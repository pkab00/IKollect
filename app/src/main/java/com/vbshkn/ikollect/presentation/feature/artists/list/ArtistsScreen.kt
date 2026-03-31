package com.vbshkn.ikollect.presentation.feature.artists.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.ArtistOverview
import com.vbshkn.ikollect.presentation.composable.CardGrid
import com.vbshkn.ikollect.presentation.composable.EmptyCardGridFiller
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.SmallTextLabel
import com.vbshkn.ikollect.util.PaletteUtil
import com.vbshkn.ikollect.util.UiText

@Composable
fun AccountScreen(
    viewModel: ArtistsViewModel,
    onShowAllGroupsClick: () -> Unit,
    onShowAllSoloistsClick: () -> Unit,
    onArtistClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopBar() },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp)
        ) {
            SectionWrapper(
                title = stringResource(R.string.title_groups),
                onAction = onShowAllGroupsClick
            ) {
                val items = uiState.groupOverviews
                if (items.isEmpty()) {
                    EmptyCardGridFiller()
                } else {
                    CardGrid {
                        items(
                            items = items,
                            key = { it.artistId }
                        ) { overview ->
                            ArtistBox(
                                overview = overview,
                                onClick = onArtistClick
                            )
                        }
                    }
                }
            }

            SectionWrapper(
                title = stringResource(R.string.title_soloists),
                onAction = onShowAllSoloistsClick
            ) {
                val items = uiState.soloistsOverviews
                if (items.isEmpty()) {
                    EmptyCardGridFiller()
                } else {
                    CardGrid {
                        items(
                            items = items,
                            key = { it.artistId }
                        ) { overview ->
                            ArtistBox(
                                overview = overview,
                                onClick = onArtistClick
                            )
                        }
                    }
                }
            }
        }
    }
    if (uiState.isLoading) {
        LoadingOverlay()
    }
}

@Composable
private fun SectionWrapper(
    title: String,
    onAction: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.label_show_all),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.clickable { onAction() }
            )
        }
        HorizontalDivider(Modifier.fillMaxWidth())
        content()
    }
}

@Composable
private fun ArtistBox(
    overview: ArtistOverview,
    onClick: (Long) -> Unit
) {
    val initialColors = listOf(
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
    var imageGradient by remember { mutableStateOf(Brush.verticalGradient(initialColors)) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(120.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick(overview.artistId) }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(overview.imageUrl)
                    .allowHardware(false)
                    .build(),
                onSuccess = { result ->
                    val bitmap = result.result.image.toBitmap()
                    imageGradient = PaletteUtil.getVividGradient(
                        bitmap = bitmap,
                        defaultColors = initialColors
                    )
                },
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
                    .background(imageGradient)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                SmallTextLabel(
                    text = UiText.DynamicString(
                        "${stringResource(R.string.label_albums_count)} ${overview.albumsCount}\n"
                                + "${stringResource(R.string.label_photocards_count)} ${overview.photocardsCount}"
                    )
                )
                Text(
                    text = overview.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.screen_title_artists),
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}