package com.vbshkn.ikollect.presentation.feature.albums.profile

import android.widget.Toast
import com.vbshkn.ikollect.presentation.feature.albums.profile.AlbumProfileContract.Effect
import com.vbshkn.ikollect.presentation.feature.albums.profile.AlbumProfileContract.Event
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.PullToRefreshContainer
import com.vbshkn.ikollect.presentation.composable.dialog.ConfirmDeleteDialog
import com.vbshkn.ikollect.presentation.composable.profile.ArtistList
import com.vbshkn.ikollect.presentation.composable.profile.InfoRowItem
import com.vbshkn.ikollect.presentation.composable.profile.NotesField
import com.vbshkn.ikollect.presentation.composable.profile.PhotocardList
import com.vbshkn.ikollect.presentation.composable.profile.ProfileInfoSection
import com.vbshkn.ikollect.presentation.composable.profile.ProfileItemWrapper
import com.vbshkn.ikollect.presentation.composable.profile.ProfileScaffold
import com.vbshkn.ikollect.presentation.composable.profile.StatCard
import com.vbshkn.ikollect.presentation.composable.profile.rememberProfileTopBarState
import com.vbshkn.ikollect.util.TimeUtil.toDateString
import com.vbshkn.ikollect.util.UiText

@Composable
fun AlbumProfileScreen(
    viewModel: AlbumProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    onNavigateToPhotocard: (Long) -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    val topBarState = rememberProfileTopBarState()

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onNavigateBack()
                is Effect.NavigateToArtist -> onNavigateToArtist(effect.id)
                is Effect.NavigateToPhotocard -> onNavigateToPhotocard(effect.id)
                is Effect.NavigateToEdit -> onNavigateToEdit()
                is Effect.ShowRefreshingErrorToast -> {
                    Toast.makeText(context, R.string.message_unable_to_refresh, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DialogHost(uiState.dialogState, viewModel::onEvent)

    val totalPhotocards = (profile?.photocards?.size ?: 0).toString()
    val statItems = listOf(
        StatCard.ImageStatCardItem(
            imageUrl = profile?.album?.artists[0]?.profileImage,
            label = UiText.DynamicString(profile?.album?.artists[0]?.name ?: ""),
            onClick = { viewModel.onEvent(Event.OnOwnerClicked) }
        ),
        StatCard.TextStatCardItem(
            label = UiText.StringResource(R.string.artist_profile_title_photocards),
            value = UiText.DynamicString(totalPhotocards),
            painter = painterResource(R.drawable.ic_photocards)
        )
    )
    val infoItems = listOf(
        InfoRowItem(
            label = UiText.StringResource(R.string.album_profile_label_title),
            value = UiText.DynamicString(profile?.album?.name ?: "-:-"),
            isLongText = true
        ),
        InfoRowItem(
            label = UiText.StringResource(R.string.album_profile_label_version),
            value = UiText.DynamicString(profile?.album?.version ?: "-:-"),
            isLongText = true
        ),
        InfoRowItem(
            label = UiText.StringResource(R.string.album_profile_label_artists),
            value = UiText.DynamicString(profile?.album?.artists?.joinToString { it.name } ?: "-:-"),
            isLongText = true
        ),
        InfoRowItem(
            label = UiText.StringResource(R.string.album_profile_label_release_year),
            value = UiText.DynamicString(profile?.album?.releaseDate ?: "-:-"),
        ),
        InfoRowItem(
            label = UiText.StringResource(R.string.album_profile_label_komca),
            value = UiText.DynamicString(profile?.album?.komcaNumber ?: "-:-"),
        ),
        InfoRowItem(
            label = UiText.StringResource(R.string.album_profile_label_added_on),
            value = UiText.DynamicString(profile?.album?.savingTimestamp?.toDateString() ?: "-:-"),
        )
    )

    PullToRefreshContainer(
        isRefreshing = uiState.isSyncing,
        onRefresh = { viewModel.onEvent(Event.OnPulledToRefresh) }
    ) {
        ProfileScaffold(
            imageUrl = profile?.album?.coverImage,
            title = profile?.album?.name ?: "",
            like = profile?.album?.isFavorite ?: false,
            topBarState = topBarState,
            onNavigate = { viewModel.onEvent(Event.OnBackClicked) },
            onLikeToggled = {
                profile?.album?.let { viewModel.onEvent(Event.OnLikeClicked(it.albumId)) }
            },
            actions = { animatedColor ->
                IconButton(onClick = { viewModel.onEvent(Event.OnEditClicked) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = animatedColor
                    )
                }
                IconButton(onClick = { viewModel.onEvent(Event.OnDeleteClicked) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = animatedColor
                    )
                }
            }
        ) {
            item {
                ProfileItemWrapper(
                    title = UiText.StringResource(R.string.artist_profile_title_information)
                ) { ProfileInfoSection(statItems, infoItems) }
            }

            item {
                profile?.album?.artists?.let { items ->
                    if (items.size > 1) {
                        ArtistList(
                            title = UiText.StringResource(R.string.profile_title_featuring),
                            artists = items.drop(1),
                            onClick = { viewModel.onEvent(Event.OnArtistCardClicked(it)) }
                        )
                    }
                }
            }
            item {
                PhotocardList(
                    title = UiText.StringResource(R.string.artist_profile_title_photocards),
                    photocards = profile?.photocards,
                    onClick = { viewModel.onEvent(Event.OnPhotocardCardClicked(it)) }
                )
            }
            item {
                NotesField(
                    title = UiText.StringResource(R.string.profile_title_notes),
                    text = uiState.profile?.album?.userNote?.let { UiText.DynamicString(it) }
                )
            }
        }
        if (uiState.isLoading) { LoadingOverlay() }
    }
}

@Composable
fun DialogHost(
    dialog: AlbumProfileDialogState,
    onEvent: (Event) -> Unit
) {
    when (dialog) {
        AlbumProfileDialogState.ConfirmDeletion -> ConfirmDeleteDialog(
            onConfirm = { onEvent(Event.OnDeletionConfirmed) },
            onDismiss = { onEvent(Event.OnDismissDialogClicked) }
        )
        AlbumProfileDialogState.None -> {}
    }
}