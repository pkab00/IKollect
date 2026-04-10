package com.vbshkn.ikollect.presentation.composable.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.util.UiText

@Composable
fun NotesField(
    title: UiText,
    text: UiText?,
    modifier: Modifier = Modifier
) {
    ProfileItemWrapper(title = title) {
        OutlinedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                text?.asString()?.let {
                    Text(
                        text = it.ifBlank { stringResource(R.string.placeholder_no_notes) },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}