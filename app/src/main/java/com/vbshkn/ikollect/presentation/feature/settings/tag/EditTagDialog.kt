package com.vbshkn.ikollect.presentation.feature.settings.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.presentation.composable.TagLabel
import com.vbshkn.ikollect.presentation.composable.profile.WrappingTitle
import com.vbshkn.ikollect.util.UiText
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTagDialog(
    title: UiText,
    onDone: (TagItem) -> Unit,
    onDismissRequest: () -> Unit
) {

    val symbolLimit = remember { 20 }
    var tagName by remember { mutableStateOf("Sample Text") }
    var tagColor by remember {
        mutableStateOf(
            Color(
                red = Random.nextInt(256),
                green = Random.nextInt(256),
                blue = Random.nextInt(256)
            )
        )
    }
    val tag = TagItem(isSystem = false, name = UiText.DynamicString(tagName), color = tagColor)
    val controller = rememberColorPickerController()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = title.asString()) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = { onDone(tag) }) {
                            Text(
                                text = stringResource(R.string.label_done),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        TagLabel(
                            tag = tag,
                            isSelected = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .padding(horizontal = 56.dp)
                        )
                    }
                    item {
                        WrappingTitle(
                            title = UiText.StringResource(R.string.title_tag_name)
                        ) {
                            TextField(
                                value = tagName,
                                onValueChange = { if (it.length <= symbolLimit) tagName = it },
                                isError = tagName.length >= symbolLimit
                            )
                        }
                    }
                    item {
                        WrappingTitle(
                            title = UiText.StringResource(R.string.title_tag_color)
                        ) {
                            HsvColorPicker(
                                initialColor = tagColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                controller = controller,
                                onColorChanged = { colorEnvelope ->
                                    val color = colorEnvelope.color
                                    tagColor = color
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}