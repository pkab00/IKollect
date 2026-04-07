package com.vbshkn.ikollect.presentation.feature.artists.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.profile.ArtistProfileData
import com.vbshkn.ikollect.util.TimeUtil.toDateString
import com.vbshkn.ikollect.util.UiText

@Composable
fun ArtistInfoSection(
    profile: ArtistProfileData?,
    modifier: Modifier = Modifier
) {
    val totalAlbums = (profile?.albums?.size ?: 0).toString()
    val totalPhotocards = (profile?.photocards?.size ?: 0).toString()
    val status = if (profile?.artist?.isGroup == true) "Группа" else "Солист"
    val firstAlbum = profile?.albums?.minByOrNull { it.savingTimestamp }
    val lastAlbum = profile?.albums?.maxByOrNull { it.savingTimestamp }

    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ряд со статистикой (плитки)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = UiText.StringResource(R.string.artist_profile_title_albums),
                value = UiText.DynamicString(totalAlbums),
                painter = painterResource(R.drawable.ic_albums),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = UiText.StringResource(R.string.artist_profile_title_photocards),
                value = UiText.DynamicString(totalPhotocards),
                painter = painterResource(R.drawable.ic_photocards),
                modifier = Modifier.weight(1f)
            )
        }

        // Основной блок информации
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                InfoRow(
                    label = UiText.StringResource(R.string.artist_profile_label_status),
                    value = UiText.DynamicString(status)
                )
                RowDivider()
                InfoRow(
                    label = UiText.StringResource(R.string.artist_profile_label_statred_on),
                    value = UiText.DynamicString(firstAlbum?.savingTimestamp?.toDateString() ?: "-:-")
                )
                RowDivider()
                InfoRow(
                    label = UiText.StringResource(R.string.artist_profile_label_last_update),
                    value = UiText.DynamicString(lastAlbum?.savingTimestamp?.toDateString() ?: "-:-"))
                RowDivider()
                InfoRow(
                    label = UiText.StringResource(R.string.artist_profile_label_first_album),
                    value = UiText.DynamicString(firstAlbum?.extendedName ?: "-:-"),
                    isLongText = true
                )
                RowDivider()
                InfoRow(
                    label = UiText.StringResource(R.string.artist_profile_label_latest_album),
                    value = UiText.DynamicString(lastAlbum?.extendedName ?: "-:-"),
                    isLongText = true
                )
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
private fun StatCard(
    label: UiText,
    value: UiText,
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.padding(16.dp), horizontalAlignment = Alignment.Start
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(text = value.asString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = label.asString(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InfoRow(
    label: UiText,
    value: UiText,
    isLongText: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = if (isLongText) Alignment.Top else Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.asString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}