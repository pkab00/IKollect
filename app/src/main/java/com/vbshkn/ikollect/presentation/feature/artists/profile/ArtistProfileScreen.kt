package com.vbshkn.ikollect.presentation.feature.artists.profile

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.Album
import com.vbshkn.ikollect.domain.model.Artist
import com.vbshkn.ikollect.domain.model.ArtistProfileData
import com.vbshkn.ikollect.domain.model.Photocard
import com.vbshkn.ikollect.presentation.composable.CardGrid
import com.vbshkn.ikollect.presentation.composable.EmptyCardGridFiller
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.presentation.composable.ProfileItemWrapper
import com.vbshkn.ikollect.presentation.composable.SmallTextLabel
import com.vbshkn.ikollect.util.PaletteUtil
import com.vbshkn.ikollect.util.TimeUtil.toDateString
import com.vbshkn.ikollect.util.UiText
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarState
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun ArtistProfileScreen(
    viewModel: ArtistProfileViewModel,
    onAnotherArtistClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val state = rememberCollapsingToolbarScaffoldState()
    val profile = uiState.profileData

    val expandedHeight = 300.dp
    val collapsedHeight = 56.dp

    CollapsingToolbarScaffold(
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbarModifier = Modifier.background(MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxSize(),
        toolbar = {
            ArtistCollapsingToolbar(
                toolbarState = state.toolbarState,
                title = profile?.artist?.name ?: "",
                imageUrl = profile?.artist?.profileImage,
                onBackClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(expandedHeight)
                    .parallax(0.5f),
                textModifier = Modifier
                    .road(
                        whenCollapsed = Alignment.CenterStart,
                        whenExpanded = Alignment.BottomStart
                    )
                    .padding(start = 56.dp, end = 16.dp, top = collapsedHeight, bottom = 16.dp),
                backModifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(collapsedHeight)
                    .pin()
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp)
        ) {
            item {
                ProfileItemWrapper(
                    title = UiText.StringResource(R.string.artist_profile_title_information)
                ) { ArtistInfoSection(profile) }
            }
            item {
                when(profile) {
                    is ArtistProfileData.GroupProfile -> {
                        ProfileItemWrapper(
                            title = UiText.StringResource(R.string.artist_profile_title_members),
                            enabled = profile.members.isNotEmpty()
                        ) {
                            CardGrid(
                                height = 200.dp,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                items(profile.members) { member ->
                                    MemberOrGroupCard(
                                        artist = member,
                                        onClick = onAnotherArtistClick
                                    )
                                }
                            }
                        }
                    }
                    is ArtistProfileData.SoloistProfile -> {
                        ProfileItemWrapper(
                            title = UiText.StringResource(R.string.artist_profile_title_in_groups),
                            enabled = profile.groups.isNotEmpty()
                        ) {
                            CardGrid(
                                height = 200.dp,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                items(profile.groups) { group ->
                                    MemberOrGroupCard(
                                        artist = group,
                                        onClick = onAnotherArtistClick
                                    )
                                }
                            }
                        }
                    }
                    null -> {}
                }
            }
            item {
                ProfileItemWrapper(
                    title = UiText.StringResource(R.string.artist_profile_title_albums)
                ) {
                    if (!profile?.albums.isNullOrEmpty()) {
                        CardGrid(
                            height = 275.dp,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            items(profile.albums) { album ->
                                AlbumCard(album) {}
                            }
                        }
                    } else { EmptyCardGridFiller() }
                }
            }
            item {
                ProfileItemWrapper(
                    title = UiText.StringResource(R.string.artist_profile_title_photocards)
                ) {
                    if (!profile?.photocards.isNullOrEmpty()) {
                        CardGrid(
                            height = 275.dp,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            items(profile.photocards) { photocard ->
                                PhotocardCard(photocard) {}
                            }
                        }
                    } else { EmptyCardGridFiller() }
                }
            }
        }
    }
    if (uiState.isLoading) {
        LoadingOverlay()
    }
}

@Composable
fun PhotocardCard(
    photocard: Photocard,
    onClick: (Long) -> Unit
) {
    // TODO
}

@Composable
private fun AlbumCard(
    album: Album,
    onClick: (Long) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(height = 180.dp, width = 160.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick(album.albumId) }
        ) {
            AsyncImage(
                model = album.coverImage,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.weight(0.8f)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                Text(
                    text = album.name,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                SmallTextLabel(
                    text = UiText.DynamicString(album.version),
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                )
                SmallTextLabel(
                    text = UiText.DynamicString(album.savingTimestamp.toDateString()),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MemberOrGroupCard(
    artist: Artist,
    onClick: (Long) -> Unit
) {
    val initialColors = listOf(
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
    var imageGradient by remember { mutableStateOf(Brush.verticalGradient(initialColors)) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(height = 180.dp, width = 160.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick(artist.artistId) }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(artist.profileImage)
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
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .weight(0.8f)
                    .background(imageGradient)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(0.2f)
                    .padding(6.dp)
            ) {
                Text(
                    text = artist.name,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
private fun ArtistCollapsingToolbar(
    toolbarState: CollapsingToolbarState,
    title: String,
    imageUrl: String?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    backModifier: Modifier = Modifier,
) {
    val textSize = (20f + (12f * toolbarState.progress)).sp


    // Основной фон с картинкой
    Box(
        modifier = modifier
    ) {
        // Фотография артиста
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Градиент
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        ),
                        startY = 300f
                    )
                )
        )

        // Дополнительное затемнение при сворачивании
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f - toolbarState.progress))
        )
    }

    // 3. Анимированный заголовок (Имя)
    Text(
        text = title,
        color = if (toolbarState.progress < 0.3f)
            MaterialTheme.colorScheme.onSurfaceVariant
        else Color.White,
        fontSize = textSize,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = textModifier
    )

    // 4. Кнопка "Назад" (закреплена всегда сверху)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = backModifier
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = if (toolbarState.progress < 0.3f) MaterialTheme.colorScheme.onSurfaceVariant
                else Color.White
            )
        }
    }
}