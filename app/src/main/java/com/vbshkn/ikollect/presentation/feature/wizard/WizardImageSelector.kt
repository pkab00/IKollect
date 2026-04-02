package com.vbshkn.ikollect.presentation.feature.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.util.PaletteUtil

@Composable
fun WizardImageSelector(
    imageUrl: String?,
    onSelectPicture: () -> Unit,
    onTakePicture: () -> Unit,
    imagePreview: @Composable (String?) -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(
                    if (imageUrl != null) R.string.add_details_caption_has_cover
                    else R.string.add_details_caption_no_image
                ),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onSelectPicture) {
                    Text(text = stringResource(R.string.add_details_select_picture))
                }
                Button(onClick = onTakePicture) {
                    Text(text = stringResource(R.string.add_details_take_picture))
                }
            }
            imagePreview(imageUrl)
        }
    }
}

@Composable
fun ImageSelectorPreview(
    imageUrl: String?,
    modifier: Modifier
) {
    var boxGradient: Brush by remember {
        mutableStateOf(Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(boxGradient)
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .allowHardware(false)
                    .build(),
                onSuccess = { result ->
                    val bitmap = result.result.image.toBitmap()
                    boxGradient = PaletteUtil.getSoftGradient(
                        bitmap = bitmap,
                        defaultColors = listOf(Color.Transparent, Color.Transparent)
                    )
                },
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }
    }
}