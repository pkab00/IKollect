package com.vbshkn.ikollect.presentation.feature.artists.list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.business.ArtistFilter
import com.vbshkn.ikollect.presentation.composable.CommonTopBar
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.PullToRefreshContainer
import com.vbshkn.ikollect.presentation.composable.SelectableLabel
import com.vbshkn.ikollect.presentation.composable.grid.ArtistsGrid
import com.vbshkn.ikollect.presentation.feature.albums.list.AlbumsContract
import com.vbshkn.ikollect.presentation.feature.artists.list.ArtistsContract.Event
import com.vbshkn.ikollect.util.UiText

@Composable
fun ArtistsScreen(
    viewModel: ArtistsViewModel,
    onNavigateToArtist: (Long) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ArtistsContract.Effect.ShowRefreshingErrorToast -> {
                    Toast.makeText(context, R.string.message_unable_to_refresh, Toast.LENGTH_SHORT).show()
                }

                is ArtistsContract.Effect.NavigateToArtist -> onNavigateToArtist(effect.artistId)
                is ArtistsContract.Effect.NavigateToSearch -> onNavigateToSearch()
            }
        }
    }

    PullToRefreshContainer(
        isRefreshing = uiState.isSyncing,
        onRefresh = { viewModel.onEvent(Event.OnPulledToRefresh) }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = UiText.StringResource(R.string.screen_title_artists),
                    counter = uiState.artists.size,
                    actions = {
                        IconButton({ viewModel.onEvent(Event.OnSearchClick) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    }
                )
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
            ) {
                if (uiState.artists.isEmpty() && !uiState.isLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(R.string.message_no_albums_found),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            items(
                                items = listOf(
                                    ArtistFilter.GROUPS,
                                    ArtistFilter.SOLOISTS
                                ),
                                key = { it.name }
                            ) { filter ->
                                SelectableLabel(
                                    text = when (filter) {
                                        ArtistFilter.GROUPS -> UiText.StringResource(R.string.title_groups)
                                        ArtistFilter.SOLOISTS -> UiText.StringResource(R.string.title_soloists)
                                        ArtistFilter.ALL -> UiText.DynamicString("")
                                    },
                                    imageVector = when (filter) {
                                        ArtistFilter.GROUPS -> Icons.Default.Groups
                                        ArtistFilter.SOLOISTS -> Icons.Default.Person
                                        ArtistFilter.ALL -> null
                                    },
                                    selected = uiState.artistFilter == filter,
                                    onClick = { viewModel.onEvent(Event.OnSelectFilter(filter)) },
                                )
                            }
                        }
                        if (uiState.artists.isEmpty()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(text = stringResource(R.string.filler_nothing_to_show))
                            }
                        } else {
                            ArtistsGrid(
                                items = uiState.artists,
                                onClick = { viewModel.onEvent(Event.OnArtistClick(it.artistId)) },
                            )
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