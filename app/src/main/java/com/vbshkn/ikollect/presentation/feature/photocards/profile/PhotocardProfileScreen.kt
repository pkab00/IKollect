package com.vbshkn.ikollect.presentation.feature.photocards.profile

import android.widget.Toast
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileContract.Event
import com.vbshkn.ikollect.presentation.feature.photocards.profile.PhotocardProfileContract.Effect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.PullToRefreshContainer
import com.vbshkn.ikollect.presentation.composable.TagsField
import com.vbshkn.ikollect.presentation.composable.dialog.ConfirmDeleteDialog
import com.vbshkn.ikollect.presentation.composable.profile.ArtistList
import com.vbshkn.ikollect.presentation.composable.profile.InfoRowItem
import com.vbshkn.ikollect.presentation.composable.profile.NotesField
import com.vbshkn.ikollect.presentation.composable.profile.ProfileInfoSection
import com.vbshkn.ikollect.presentation.composable.profile.ProfileItemWrapper
import com.vbshkn.ikollect.presentation.composable.profile.ProfileScaffold
import com.vbshkn.ikollect.presentation.composable.profile.StatCard
import com.vbshkn.ikollect.presentation.composable.profile.rememberProfileTopBarState
import com.vbshkn.ikollect.presentation.feature.photocards.wizard.PhotocardWizardContract
import com.vbshkn.ikollect.util.TimeUtil.toDateString
import com.vbshkn.ikollect.util.UiText

@Composable
fun PhotocardProfileScreen(
    viewModel: PhotocardProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    onNavigateToAlbum: (Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    val topBarState = rememberProfileTopBarState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Effect.NavigateBack -> onNavigateBack()
                is Effect.NavigateToArtist -> onNavigateToArtist(effect.id)
                is Effect.NavigateToAlbum -> onNavigateToAlbum(effect.id)
                is Effect.NavigateToEdit -> onNavigateToEdit(effect.id)
                is Effect.ShowRefreshingErrorToast -> {
                    Toast.makeText(context, R.string.message_unable_to_refresh, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    DialogHost(uiState.dialogState, viewModel::onEvent)

    val statItems = listOf(
        StatCard.ImageStatCardItem(
            imageUrl = profile?.photocard?.owner?.profileImage,
            label = UiText.DynamicString(profile?.photocard?.owner?.name ?: "-:-"),
            onClick = { viewModel.onEvent(Event.OnOwnerCardClicked) }
        ),
        StatCard.ImageStatCardItem(
            imageUrl = profile?.album?.coverImage,
            label = profile?.album?.name?.let { UiText.DynamicString(it) }
                ?: UiText.StringResource(R.string.no_album_placeholder),
            onClick = { viewModel.onEvent(Event.OnAlbumCardClicked) }
        )
    )

    val infoItems = listOf(
        InfoRowItem(
            label = UiText.StringResource(R.string.profile_label_name),
            value = UiText.DynamicString(profile?.photocard?.displayName ?: "-:-"),
            isLongText = true
        ),
        InfoRowItem(
            label = UiText.StringResource(R.string.album_profile_label_added_on),
            value = UiText.DynamicString(
                profile?.photocard?.savingTimestamp?.toDateString() ?: "-:-"
            )
        )
    )

    PullToRefreshContainer(
        isRefreshing = uiState.isSyncing,
        onRefresh = { viewModel.onEvent(Event.OnPulledToRefresh) }
    ) {
        ProfileScaffold(
            imageUrl = profile?.photocard?.imageUrl,
            title = profile?.photocard?.displayName ?: "",
            like = profile?.photocard?.isFavorite ?: false,
            topBarState = topBarState,
            onNavigate = { viewModel.onEvent(Event.OnBackClicked) },
            onLikeToggled = {
                profile?.photocard?.let {
                    viewModel.onEvent(Event.OnLikeClicked(it.photocardId, it.isFavorite))
                }
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
                ProfileInfoSection(statItems, infoItems)
            }
            item {
                ArtistList(
                    title = UiText.StringResource(R.string.wizard_title_on_the_card),
                    artists = profile?.depictedArtists ?: emptyList(),
                    onClick = { viewModel.onEvent(Event.OnArtistCardClicked(it)) }
                )
            }
            item {
                ProfileItemWrapper(
                    title = UiText.StringResource(R.string.profile_label_tags)
                ) {
                    TagsField(
                        tags = uiState.profile?.photocard?.tags ?: emptyList(),
                        onTagClick = { },
                    )
                }
            }
            item {
                NotesField(
                    title = UiText.StringResource(R.string.artist_profile_title_notes),
                    text = profile?.photocard?.userNotes?.let { UiText.DynamicString(it) }
                )
            }
        }
    }
}

@Composable
fun DialogHost(
    dialog: PhotocardProfileDialogState,
    onEvent: (Event) -> Unit
) {
    when (dialog) {
        PhotocardProfileDialogState.ConfirmDeletion -> ConfirmDeleteDialog(
            onConfirm = { onEvent(Event.OnDeletionConfirmed) },
            onDismiss = { onEvent(Event.OnDismissDialogClicked) }
        )
        PhotocardProfileDialogState.None -> {}
    }
}