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
import com.vbshkn.ikollect.presentation.composable.PhotocardItem
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
                height = 220.dp,
                modifier = Modifier.padding(8.dp)
            ) {
                items(photocards) { photocard ->
                    PhotocardItem(
                        item = photocard,
                        height = 200.dp,
                        onClick = { onClick(photocard.photocardId) }
                    )
                }
            }
        } else {
            EmptyCardGridFiller()
        }
    }
}