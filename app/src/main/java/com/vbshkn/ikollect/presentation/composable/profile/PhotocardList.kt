package com.vbshkn.ikollect.presentation.composable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.presentation.composable.CardGrid
import com.vbshkn.ikollect.presentation.composable.EmptyCardGridFiller
import com.vbshkn.ikollect.presentation.composable.TagLabel
import com.vbshkn.ikollect.util.UiText

@Composable
fun PhotocardList(
    title: UiText,
    photocards: List<PhotocardListItem>?,
    onClick: (Long) -> Unit
) {
    ProfileItemWrapper(
        title = title
    ) {
        if (!photocards.isNullOrEmpty()) {
            CardGrid(
                height = 275.dp,
                modifier = Modifier.padding(8.dp)
            ) {
                items(photocards) { photocard ->
                    PhotocardCard(photocard, onClick)
                }
            }
        } else {
            EmptyCardGridFiller()
        }
    }
}

@Composable
private fun PhotocardCard(
    photocard: PhotocardListItem,
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
                .clickable { onClick(photocard.photocardId) }
        ) {
            AsyncImage(
                model = photocard.imageUrl,
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
                    text = photocard.displayName,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tagsLimit = 2
                    val displayTags = photocard.tags.take(tagsLimit)
                    val remainingTags = photocard.tags.size - tagsLimit
                    displayTags.forEach { tag ->
                        TagLabel(
                            tag = tag,
                            isSelected = false,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    if (remainingTags > 0) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(26.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    CircleShape
                                )
                        ) {
                            Text(
                                text = "+$remainingTags",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}