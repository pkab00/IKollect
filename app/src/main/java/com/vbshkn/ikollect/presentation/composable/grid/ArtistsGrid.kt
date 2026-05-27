package com.vbshkn.ikollect.presentation.composable.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.domain.model.list.ArtistListItem
import com.vbshkn.ikollect.presentation.composable.ArtistBox

@Composable
fun ArtistsGrid(
    items: List<ArtistListItem>,
    onClick: (ArtistListItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = items,
            key = { it.artistId }
        ) { artist ->
            ArtistBox(
                overview = artist,
                onClick = { onClick(artist) },
                modifier = Modifier
                    .size(140.dp)
                    .animateItem()
            )
        }
    }
}