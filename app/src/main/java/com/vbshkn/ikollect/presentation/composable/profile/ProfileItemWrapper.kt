package com.vbshkn.ikollect.presentation.composable.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.util.UiText

@Composable
fun ProfileItemWrapper(
    title: UiText,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showHint: Boolean = false,
    onHint: () -> Unit = {},
    showAction: Boolean = false,
    actionText: UiText? = null,
    onAction: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    if (enabled) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = title.asString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
                if (showHint) {
                    Box(modifier = Modifier.size(24.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_info),
                            contentDescription = null,
                            modifier = Modifier.clickable { onHint() }
                        )
                    }
                }
                else if (showAction && actionText != null) {
                    Text(
                        text = actionText.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onAction() }
                    )
                }
            }
            if (showHint || showAction) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )
            }
            content()
        }
    }
}