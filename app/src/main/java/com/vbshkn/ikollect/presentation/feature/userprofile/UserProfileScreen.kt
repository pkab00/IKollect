package com.vbshkn.ikollect.presentation.feature.userprofile

import com.vbshkn.ikollect.presentation.feature.userprofile.UserProfileContract.Effect
import com.vbshkn.ikollect.presentation.feature.userprofile.UserProfileContract.Event
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.AppUser
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.presentation.composable.AlbumCard
import com.vbshkn.ikollect.presentation.composable.ArtistBox
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.PhotocardItem
import com.vbshkn.ikollect.util.UiText
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarState
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalToolbarApi::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToPhotocard: (Long) -> Unit,
    onNavigateToArtist: (Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn = state.user?.email != null
    val scaffoldState = rememberCollapsingToolbarScaffoldState()
    val tabs = Tabs.entries.toList()
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Effect.GoToAuthScreen -> onNavigateToAuth()
                is Effect.GoToAlbum -> onNavigateToAlbum(effect.id)
                is Effect.GoToArtist -> onNavigateToArtist(effect.id)
                is Effect.GoToPhotocard -> onNavigateToPhotocard(effect.id)
                is Effect.GoToSettings -> onNavigateToSettings()
            }
        }
    }

    LaunchedEffect(state.user) {
        if (state.user != null) {
            scaffoldState.toolbarState.expand()
        }
    }

    CollapsingToolbarScaffold(
        state = scaffoldState,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        toolbar = {
            state.user?.let {
                ProfileHeader(
                    username = it.username,
                    uid = it.uid,
                    profilePicture = it.profilePictureUrl,
                    email = it.email,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .fillMaxWidth()
                        .padding(top = 116.dp, bottom = 16.dp)
                        .parallax(0.5f)
                )
            }
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.onEvent(Event.OnSettingsClick) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.pin()
            )
        }
    ) {
        if (isLoggedIn) {
            val scope = rememberCoroutineScope()
            val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

            Column(modifier = Modifier.fillMaxSize()) {
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            unselectedContentColor = MaterialTheme.colorScheme.outline,
                            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(tab.ordinal) }
                            },
                            text = {
                                Text(
                                    text = tab.text.asString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    when (Tabs.entries[pagerState.currentPage]) {
                        Tabs.ALBUMS -> {
                            AlbumTabContent(
                                albums = state.favoriteAlbums,
                                onClick = { viewModel.onEvent(Event.OnAlbumClick(it)) })
                        }

                        Tabs.PHOTOCARDS -> {
                            PhotocardTabContent(
                                photocards = state.favoritePhotocards,
                                onClick = { viewModel.onEvent(Event.OnPhotocardClick(it)) }
                            )
                        }

                        Tabs.ARTISTS -> {
                            ArtistTabContent(
                                artists = state.favoriteArtists,
                                onClick = { viewModel.onEvent(Event.OnArtistClick(it)) }
                            )
                        }
                    }
                }
            }
        } else {
            LoggedOutContent(onLoginClick = { viewModel.onEvent(Event.OnLogInClick) })
        }
    }
    if (state.isLoading) {
        LoadingOverlay()
    }
}

@Composable
fun LoggedOutContent(
    onLoginClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.filler_login),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onLoginClick) {
            Text(text = stringResource(R.string.action_login))
        }
    }
}

@Composable
private fun ProfileHeader(
    username: String?,
    uid: String?,
    profilePicture: String?,
    email: String?,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        AsyncImage(
            model = profilePicture ?: R.drawable.default_avatar,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(size = 75.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = username ?: "user${uid?.split("-")?.first()}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun AlbumTabContent(
    albums: List<AlbumDetails>,
    onClick: (Long) -> Unit

) {
    if (albums.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.filler_nothing_to_show))
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = albums,
                key = { it.albumId }
            ) { album ->
                AlbumCard(
                    album = album,
                    onClick = { onClick(album.albumId) }
                )
            }
        }
    }
}

@Composable
private fun PhotocardTabContent(
    photocards: List<PhotocardListItem>,
    onClick: (Long) -> Unit
) {
    if (photocards.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.filler_nothing_to_show))
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = photocards,
                key = { it.photocardId }
            ) { photocard ->
                PhotocardItem(
                    item = photocard,
                    height = 150.dp,
                    onClick = { onClick(photocard.photocardId) }
                )
            }
        }
    }
}

@Composable
private fun ArtistTabContent(
    artists: List<ArtistListItem>,
    onClick: (Long) -> Unit
) {
    if (artists.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.filler_nothing_to_show))
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = artists,
                key = { it.artistId }
            ) { artist ->
                ArtistBox(
                    overview = artist,
                    onClick = { onClick(artist.artistId) },
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}

private enum class Tabs(val text: UiText) {
    ALBUMS(UiText.StringResource(R.string.screen_title_albums)),
    PHOTOCARDS(UiText.StringResource(R.string.screen_title_photocards)),
    ARTISTS(UiText.StringResource(R.string.screen_title_artists)),
}