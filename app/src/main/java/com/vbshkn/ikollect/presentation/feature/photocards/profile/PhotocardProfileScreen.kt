package com.vbshkn.ikollect.presentation.feature.photocards.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.presentation.composable.TagLabel
import com.vbshkn.ikollect.presentation.composable.profile.ArtistList
import com.vbshkn.ikollect.presentation.composable.profile.InfoRowItem
import com.vbshkn.ikollect.presentation.composable.profile.NotesField
import com.vbshkn.ikollect.presentation.composable.profile.ProfileInfoSection
import com.vbshkn.ikollect.presentation.composable.profile.ProfileItemWrapper
import com.vbshkn.ikollect.presentation.composable.profile.ProfileScaffold
import com.vbshkn.ikollect.presentation.composable.profile.StatCard
import com.vbshkn.ikollect.presentation.composable.profile.rememberProfileTopBarState
import com.vbshkn.ikollect.util.TimeUtil.toDateString
import com.vbshkn.ikollect.util.UiText

@Composable
fun PhotocardProfileScreen(
    viewModel: PhotocardProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    onNavigateToAlbum: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    val topBarState = rememberProfileTopBarState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PhotocardProfileContract.Effect.NavigateBack -> onNavigateBack()
                is PhotocardProfileContract.Effect.NavigateToArtist -> onNavigateToArtist(effect.id)
                is PhotocardProfileContract.Effect.NavigateToAlbum -> onNavigateToAlbum(effect.id)
            }
        }
    }

    val statItems = listOf(
        StatCard.ImageStatCardItem(
            imageUrl = profile?.photocard?.owner?.profileImage,
            label = UiText.DynamicString(profile?.photocard?.owner?.name ?: "-:-"),
            onClick = { viewModel.onEvent(PhotocardProfileContract.Event.OnOwnerCardClicked) }
        ),
        StatCard.ImageStatCardItem(
            imageUrl = profile?.album?.coverImage,
            label = profile?.album?.name?.let { UiText.DynamicString(it) }
                ?: UiText.StringResource(R.string.no_album_placeholder),
            onClick = { viewModel.onEvent(PhotocardProfileContract.Event.OnAlbumCardClicked) }
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
            value = UiText.DynamicString(profile?.photocard?.savingTimestamp?.toDateString() ?: "-:-")
        )
    )

    ProfileScaffold(
        imageUrl = profile?.photocard?.imageUrl,
        title = profile?.photocard?.displayName ?: "",
        topBarState = topBarState,
        onNavigate = { viewModel.onEvent(PhotocardProfileContract.Event.OnBackClicked) },
        actions = { animatedColor ->
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Edit,
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
                onClick = { viewModel.onEvent(PhotocardProfileContract.Event.OnArtistCardClicked(it)) }
            )
        }
        item {
            ProfileItemWrapper(
                title = UiText.StringResource(R.string.profile_label_tags)
            ) {
                TagsPanel(tags = profile?.photocard?.tags ?: emptyList())
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

@Composable
fun TagsPanel(tags: List<TagItem>) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        if (tags.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.placeholder_no_tags),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        else {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Adaptive(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalItemSpacing = 6.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                items(
                    items = tags,
                    key = { it.id }
                ) { tag ->
                    TagLabel(
                        tag = tag,
                        isSelected = false,
                        modifier = Modifier
                            .widthIn(min = 60.dp)
                            .height(20.dp)
                    )
                }
            }
        }
    }
}
