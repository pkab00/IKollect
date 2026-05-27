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
import com.vbshkn.ikollect.domain.model.details.AlbumDetails
import com.vbshkn.ikollect.presentation.composable.AlbumCard

@Composable
fun AlbumsGrid(
    items: List<AlbumDetails>,
    onClick: (AlbumDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = items,
            key = { it.albumId }
        ) { album ->
            AlbumCard(
                album = album,
                onClick = { onClick(album) },
                modifier = Modifier.animateItem()
            )
        }
    }
}