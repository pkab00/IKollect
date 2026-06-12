package com.vbshkn.ikollect.presentation.composable.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.util.UiText

@Composable
fun WrappingTitle(
    title: UiText,
    modifier: Modifier = Modifier,
    subTitle: UiText? = null,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = title.asString(),
            style = MaterialTheme.typography.titleMedium
        )
        if (subTitle != null) {
            Text(
                text = subTitle.asString(),
                style = MaterialTheme.typography.labelMedium
            )
        }
        content()
    }
}