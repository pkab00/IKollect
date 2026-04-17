package com.vbshkn.ikollect.presentation.composable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vbshkn.ikollect.util.UiText

@Composable
fun PlainTextField(
    value: String,
    title: UiText,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    limit: Int = 120,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        label = { Text(text = title.asString()) },
        onValueChange = { newValue ->
            if (newValue.length <= limit) {
                onValueChange(newValue)
            }
        },
        isError = value.length >= limit,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}