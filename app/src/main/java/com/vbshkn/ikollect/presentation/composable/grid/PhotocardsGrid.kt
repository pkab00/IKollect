package com.vbshkn.ikollect.presentation.composable.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.presentation.composable.PhotocardItem

@Composable
fun PhotocardsGrid(
    items: List<PhotocardListItem>,
    onClick: (PhotocardListItem) -> Unit,
    onHold: (PhotocardListItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = items,
            key = { it.photocardId }
        ) { photocard ->
            PhotocardItem(
                item = photocard,
                height = 150.dp,
                onClick = { onClick(photocard) },
                onHold = { onHold(photocard) },
                modifier = Modifier.animateItem()
            )
        }
    }
}