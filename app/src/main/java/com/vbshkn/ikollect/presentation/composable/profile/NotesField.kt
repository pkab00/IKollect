package com.vbshkn.ikollect.presentation.composable.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.util.UiText

@Composable
fun NotesField(
    title: UiText,
    text: UiText,
    modifier: Modifier = Modifier
) {
    ProfileItemWrapper(title = title) {
        OutlinedTextField(
            value = text.asString(),
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = TextFieldDefaults.colors().unfocusedIndicatorColor
            ),
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp)
        )
    }
}