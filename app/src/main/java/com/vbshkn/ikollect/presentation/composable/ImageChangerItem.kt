package com.vbshkn.ikollect.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.feature.wizard.ImageSelectorPreview

@Composable
fun ImageChangerItem(
    imageUrl: String?,
    onOpenGallery: () -> Unit,
    onOpenCamera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()

        ) {
            ImageSelectorPreview(
                imageUrl = imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
            ) {
                Button(onClick = onOpenGallery) {
                    Text(text = stringResource(R.string.add_details_select_picture))
                }
                Button(onClick = onOpenCamera) {
                    Text(text = stringResource(R.string.add_details_take_picture))
                }
            }
        }
    }
}