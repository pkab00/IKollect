package com.vbshkn.ikollect.presentation.feature.artists.profile

import android.widget.Toast
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileContract.Event
import com.vbshkn.ikollect.presentation.feature.artists.profile.ArtistProfileContract.Effect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.profile.ArtistProfileData
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.PullToRefreshContainer
import com.vbshkn.ikollect.presentation.composable.profile.ProfileItemWrapper
import com.vbshkn.ikollect.presentation.composable.profile.AlbumList
import com.vbshkn.ikollect.presentation.composable.profile.ArtistList
import com.vbshkn.ikollect.presentation.composable.profile.InfoRowItem
import com.vbshkn.ikollect.presentation.composable.profile.PhotocardList
import com.vbshkn.ikollect.presentation.composable.profile.ProfileInfoSection
import com.vbshkn.ikollect.presentation.composable.profile.ProfileScaffold
import com.vbshkn.ikollect.presentation.composable.profile.StatCard
import com.vbshkn.ikollect.presentation.composable.profile.rememberProfileTopBarState
import com.vbshkn.ikollect.util.TimeUtil.toDateString
import com.vbshkn.ikollect.util.UiText

@Composable
fun ArtistProfileScreen(
    viewModel: ArtistProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToPhotocard: (Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val topBarState = rememberProfileTopBarState()
    val profile = uiState.profileData

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onNavigateBack()
                is Effect.NavigateToAlbum -> onNavigateToAlbum(effect.id)
                is Effect.NavigateToArtist -> onNavigateToArtist(effect.id)
                is Effect.NavigateToPhotocard -> onNavigateToPhotocard(effect.id)
                is Effect.ShowRefreshingErrorToast -> {
                    Toast.makeText(context, R.string.message_unable_to_refresh, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val totalAlbums = (profile?.albums?.size ?: 0).toString()
    val totalPhotocards = (profile?.photocards?.size ?: 0).toString()
    val status = UiText.DynamicString(
        if (profile?.artist?.isGroup == true) stringResource(R.string.status_group) else stringResource(
            R.string.status_soloist
        )
    )
    val firstAlbum = profile?.albums?.minByOrNull { it.savingTimestamp }
    val lastAlbum = profile?.albums?.maxByOrNull { it.savingTimestamp }

    PullToRefreshContainer(
        isRefreshing = uiState.isSyncing,
        onRefresh = { viewModel.onEvent(Event.OnPulledToRefresh) }
    ) {
        ProfileScaffold(
            like = profile?.artist?.isFavorite ?: false,
            topBarState = topBarState,
            onNavigate = { viewModel.onEvent(Event.OnBackClicked) },
            onLikeToggled = {
                profile?.artist?.let {
                    viewModel.onEvent(Event.OnLikeClicked(it.artistId))
                }
            },
            imageUrl = profile?.artist?.profileImage,
            title = profile?.artist?.name ?: ""
        ) {
            item {
                val statItems = listOf(
                    StatCard.TextStatCardItem(
                        label = UiText.StringResource(R.string.artist_profile_title_albums),
                        value = UiText.DynamicString(totalAlbums),
                        painter = painterResource(R.drawable.ic_albums),
                    ),
                    StatCard.TextStatCardItem(
                        label = UiText.StringResource(R.string.artist_profile_title_photocards),
                        value = UiText.DynamicString(totalPhotocards),
                        painter = painterResource(R.drawable.ic_photocards)
                    )
                )
                val infoItems = listOf(
                    InfoRowItem(
                        label = UiText.StringResource(R.string.artist_profile_label_status),
                        value = status
                    ),
                    InfoRowItem(
                        label = UiText.StringResource(R.string.artist_profile_label_statred_on),
                        value = UiText.DynamicString(
                            firstAlbum?.savingTimestamp?.toDateString() ?: "-:-"
                        )
                    ),
                    InfoRowItem(
                        label = UiText.StringResource(R.string.artist_profile_label_last_update),
                        value = UiText.DynamicString(
                            lastAlbum?.savingTimestamp?.toDateString() ?: "-:-"
                        )
                    ),
                    InfoRowItem(
                        label = UiText.StringResource(R.string.artist_profile_label_first_album),
                        value = UiText.DynamicString(firstAlbum?.extendedName ?: "-:-"),
                        isLongText = true
                    ),
                    InfoRowItem(
                        label = UiText.StringResource(R.string.artist_profile_label_latest_album),
                        value = UiText.DynamicString(lastAlbum?.extendedName ?: "-:-"),
                        isLongText = true
                    )
                )

                ProfileItemWrapper(
                    title = UiText.StringResource(R.string.artist_profile_title_information)
                ) { ProfileInfoSection(statItems, infoItems) }
            }
            item {
                when (profile) {
                    is ArtistProfileData.GroupProfile -> {
                        ArtistList(
                            title = UiText.StringResource(R.string.artist_profile_title_members),
                            artists = profile.members,
                            onClick = { viewModel.onEvent(Event.OnArtistCardClicked(it)) }
                        )
                    }

                    is ArtistProfileData.SoloistProfile -> {
                        ArtistList(
                            title = UiText.StringResource(R.string.artist_profile_title_in_groups),
                            artists = profile.groups,
                            onClick = { viewModel.onEvent(Event.OnArtistCardClicked(it)) }
                        )
                    }

                    null -> {}
                }
            }
            item {
                AlbumList(
                    title = UiText.StringResource(R.string.artist_profile_title_albums),
                    albums = uiState.profileData?.albums,
                    onClick = { viewModel.onEvent(Event.OnAlbumCardClicked(it)) }
                )
            }
            item {
                PhotocardList(
                    title = UiText.StringResource(R.string.artist_profile_title_photocards),
                    photocards = uiState.profileData?.photocards,
                    onClick = { viewModel.onEvent(Event.OnPhotocardCardClicked(it)) }
                )
            }
        }
        if (uiState.isLoading) {
            LoadingOverlay()
        }
    }
}