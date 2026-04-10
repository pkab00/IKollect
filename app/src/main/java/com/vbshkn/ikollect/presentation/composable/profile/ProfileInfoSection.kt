package com.vbshkn.ikollect.presentation.composable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.util.UiText

@Composable
fun ProfileInfoSection(
    statCardItems: List<StatCard>,
    infoRowItems: List<InfoRowItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (item in statCardItems) {
                val cardModifier = Modifier.weight(1f).fillMaxHeight()
                when (item) {
                    is StatCard.ImageStatCardItem -> ImageStatCard(item, cardModifier)
                    is StatCard.TextStatCardItem -> TextStatCard(item, cardModifier)
                }
            }
        }
        MainBlock(infoRowItems)
    }
}

@Composable
private fun MainBlock(infoRowItems: List<InfoRowItem>) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            for (item in infoRowItems.withIndex()) {
                InfoRow(item.value)
                if (item.index != infoRowItems.size - 1) {
                    RowDivider()
                }
            }
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
private fun TextStatCard(
    item: StatCard.TextStatCardItem,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(20.dp),
        onClick = item.onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = item.painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.value.asString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.label.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ImageStatCard(
    item: StatCard.ImageStatCardItem,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(20.dp),
        onClick = item.onClick
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(
                            alpha = if (item.imageUrl == null) 1f else 0f
                        ),
                        CircleShape
                    )
                    .clip(CircleShape))
            Text(
                text = item.label.asString(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoRow(item: InfoRowItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = if (item.isLongText) Alignment.Top else Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.label.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = item.value.asString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

sealed interface StatCard {
    data class TextStatCardItem(
        val label: UiText,
        val value: UiText,
        val painter: Painter,
        val onClick: () -> Unit = {}
    ) : StatCard

    data class ImageStatCardItem(
        val imageUrl: String?,
        val label: UiText,
        val onClick: () -> Unit = {}
    ) : StatCard
}

data class InfoRowItem(
    val label: UiText,
    val value: UiText,
    val isLongText: Boolean = false
)