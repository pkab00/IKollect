package com.vbshkn.ikollect.presentation.feature.artists.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.data.local.model.pojo.ArtistMinimalDetail
import com.vbshkn.ikollect.domain.model.ArtistOverview
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay

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
                if (items.isEmpty()) { EmptyGridFiller() }
                else {
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
                if (items.isEmpty()) { EmptyGridFiller() }
                else {
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
private fun EmptyGridFiller() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.filler_nothing_to_show),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CardGrid(content: LazyGridScope.() -> Unit) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        content()
    }
}

@Composable
private fun ArtistBox(
    overview: ArtistOverview,
    onClick: (Long) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(120.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onClick(overview.artistId) }
        ) {
            AsyncImage(
                model = overview.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.weight(0.6f)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${stringResource(R.string.label_albums_count)} ${overview.albumsCount}\n"
                        +"${stringResource(R.string.label_photocards_count)} ${overview.photocardsCount}",
                        minLines = 2,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
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