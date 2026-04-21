package com.vbshkn.ikollect.presentation.composable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.presentation.composable.CardGrid
import com.vbshkn.ikollect.presentation.composable.ExpandedCardGrid
import com.vbshkn.ikollect.util.PaletteUtil
import com.vbshkn.ikollect.util.UiText

@Composable
fun ArtistList(
    title: UiText,
    artists: List<ArtistListItem>,
    onClick: (Long) -> Unit
) {
    var expand by rememberSaveable { mutableStateOf(false) }

    ProfileItemWrapper(
        title = title,
        enabled = artists.isNotEmpty(),
        showAction = true,
        actionText = UiText.StringResource(if (expand) R.string.title_collapse else R.string.title_show_all),
        onAction = { expand = !expand }
    ) {
        if (!expand) {
            CardGrid(modifier = Modifier.padding(8.dp)) {
                items(artists) { artist ->
                    ArtistCard(
                        artist = artist,
                        onClick = { onClick(artist.artistId) }
                    )
                }
            }
        }
        else {
            ExpandedCardGrid(modifier = Modifier.padding(8.dp)) {
                items(artists) { artist ->
                    ArtistCard(
                        artist = artist,
                        onClick = { onClick(artist.artistId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistCard(
    artist: ArtistListItem,
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
                    imageGradient = PaletteUtil.getVibrantGradient(
                        bitmap = bitmap,
                        defaultColors = initialColors
                    )
                },
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .weight(0.7f)
                    .background(imageGradient)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(0.3f)
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