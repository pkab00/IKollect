package com.vbshkn.ikollect.presentation.feature.artists.list

import android.widget.Toast
import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsContract.Event
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.presentation.composable.ArtistBox
import com.vbshkn.ikollect.presentation.composable.CardGrid
import com.vbshkn.ikollect.presentation.composable.CommonTopBar
import com.vbshkn.ikollect.presentation.composable.EmptyCardGridFiller
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.PullToRefreshContainer
import com.vbshkn.ikollect.util.PaletteUtil
import com.vbshkn.ikollect.util.UiText

@Composable
fun ArtistsScreen(
    viewModel: ArtistsViewModel,
    onShowAllGroupsClick: () -> Unit,
    onShowAllSoloistsClick: () -> Unit,
    onArtistClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ArtistsContract.Effect.ShowRefreshingErrorToast -> {
                    Toast.makeText(context, R.string.message_unable_to_refresh, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    PullToRefreshContainer(
        isRefreshing = uiState.isSyncing,
        onRefresh = { viewModel.onEvent(Event.OnPulledToRefresh) }
    ) {
        Scaffold(
            topBar = { CommonTopBar(title = UiText.StringResource(R.string.screen_title_artists)) },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) { paddingValues ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(10.dp)
            ) {
                item {
                    SectionWrapper(
                        title = stringResource(R.string.title_groups),
                        onAction = onShowAllGroupsClick
                    ) {
                        val items = uiState.groupOverviews
                        if (items.isEmpty()) {
                            EmptyCardGridFiller()
                        } else {
                            CardGrid(height = 160.dp) {
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

                item {
                    SectionWrapper(
                        title = stringResource(R.string.title_soloists),
                        onAction = onShowAllSoloistsClick
                    ) {
                        val items = uiState.soloistsOverviews
                        if (items.isEmpty()) {
                            EmptyCardGridFiller()
                        } else {
                            CardGrid(height = 160.dp) {
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
        }
        if (uiState.isLoading) {
            LoadingOverlay()
        }
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