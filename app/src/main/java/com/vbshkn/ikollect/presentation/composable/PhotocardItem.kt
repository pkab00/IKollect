package com.vbshkn.ikollect.presentation.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.vbshkn.ikollect.domain.model.list.PhotocardListItem
import com.vbshkn.ikollect.util.PaletteUtil
import kotlinx.coroutines.delay

@Composable
fun PhotocardItem(
    item: PhotocardListItem,
    height: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onHold: () -> Unit = {}
) {
    var innerGradient: Brush by remember {
        mutableStateOf(Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)))
    }
    val width = remember { height / 1.59f }
    val shape = remember { RoundedCornerShape(16.dp) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isLongPress by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(650)
            isLongPress = true
            onHold()
        } else {
            isLongPress = false
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedCard(
            shape = shape,
            onClick = { if (!isLongPress) onClick() },
            border = BorderStroke(0.5f.dp, MaterialTheme.colorScheme.outlineVariant),
            interactionSource = interactionSource,
            modifier = Modifier.size(width, height)
        ) {
            Box(
                Modifier.fillMaxSize().background(innerGradient)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .allowHardware(false)
                        .crossfade(true)
                        .build(),
                    onSuccess = { result ->
                        val bitmap = result.result.image.toBitmap()
                        innerGradient = PaletteUtil.getSoftGradient(
                            bitmap = bitmap,
                            defaultColors = listOf(Color.Transparent, Color.Transparent)
                        )
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                if (item.isFavorite) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Text(
            text = item.displayName,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(width)
        )
    }
}
