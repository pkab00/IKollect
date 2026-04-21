package com.vbshkn.ikollect.presentation.composable.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.presentation.composable.CardGrid
import com.vbshkn.ikollect.presentation.composable.EmptyCardGridFiller
import com.vbshkn.ikollect.presentation.composable.ExpandedCardGrid
import com.vbshkn.ikollect.presentation.composable.SmallTextLabel
import com.vbshkn.ikollect.util.TimeUtil.toDateString
import com.vbshkn.ikollect.util.UiText

@Composable
fun AlbumList(
    title: UiText,
    albums: List<AlbumDetails>?,
    onClick: (Long) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ProfileItemWrapper(
        title = title,
        showAction = true,
        actionText = UiText.StringResource(if (!expanded) R.string.title_show_all else R.string.title_collapse),
        onAction = { expanded = !expanded }
    ) {
        if (!albums.isNullOrEmpty()) {
            if (!expanded) {
                CardGrid(
                    height = 275.dp,
                    modifier = Modifier.padding(8.dp)
                ) {
                    items(albums) { album ->
                        AlbumCard(album, onClick)
                    }
                }
            }
            else {
                ExpandedCardGrid(
                    height = 600.dp,
                    modifier = Modifier.padding(8.dp)
                ) {
                    items(albums) { album ->
                        AlbumCard(album, onClick, 275.dp)
                    }
                }
            }
        } else {
            EmptyCardGridFiller()
        }
    }
}

@Composable
private fun AlbumCard(
    album: AlbumDetails,
    onClick: (Long) -> Unit,
    height: Dp = 180.dp
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(height = height, width = 160.dp),
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
                modifier = Modifier.weight(0.6f)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .weight(0.4f)
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
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                )
                SmallTextLabel(
                    text = UiText.DynamicString(album.version),
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