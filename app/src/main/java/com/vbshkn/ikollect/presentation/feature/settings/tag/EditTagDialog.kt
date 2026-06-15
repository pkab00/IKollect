package com.vbshkn.ikollect.presentation.feature.settings.tag

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.TagItem
import com.vbshkn.ikollect.presentation.composable.TagLabel
import com.vbshkn.ikollect.presentation.composable.dialog.ConfirmDeleteDialog
import com.vbshkn.ikollect.presentation.composable.profile.WrappingTitle
import com.vbshkn.ikollect.util.UiText
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTagDialog(
    title: UiText,
    allTagColors: List<Color> = emptyList(),
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
    val recentColors = if (allTagColors.size >= 5) {
        allTagColors.reversed().take(5)
    } else {
        val size = allTagColors.size
        allTagColors.reversed().take(size) + List(5 - size) { Color.Transparent }
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
                                    .height(200.dp),
                                controller = controller,
                                onColorChanged = { colorEnvelope ->
                                    val color = colorEnvelope.color
                                    tagColor = color
                                }
                            )
                        }
                        WrappingTitle(
                            title = UiText.DynamicString(""),
                            subTitle = UiText.StringResource(R.string.title_recent_colors)
                        ) {
                            ColorTileRow(
                                colors = recentColors,
                                onClick = { controller.selectByColor(it, true) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTagDialog(
    title: UiText,
    editedTag: TagItem,
    allTagColors: List<Color> = emptyList(),
    onDone: (TagItem) -> Unit,
    onDelete: (TagItem) -> Unit,
    onDismissRequest: () -> Unit
) {

    val symbolLimit = remember { 20 }
    var tagName by remember { mutableStateOf(editedTag.name) }
    var tagColor by remember { mutableStateOf(editedTag.color) }
    val recentColors = if (allTagColors.size >= 5) {
        allTagColors.reversed().take(5)
    } else {
        val size = allTagColors.size
        allTagColors.reversed().take(size) + List(5 - size) { Color.Transparent }
    }
    val tag = TagItem(id = editedTag.id, isSystem = false, name = tagName, color = tagColor)
    val controller = rememberColorPickerController()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = { onDelete(editedTag) },
            onDismiss = { showDeleteDialog = false }
        )
    }

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
                                value = tagName.asString(),
                                onValueChange = { if (it.length <= symbolLimit) tagName = UiText.DynamicString(it) },
                                isError = tagName.asString().length >= symbolLimit
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
                                    .height(200.dp),
                                controller = controller,
                                onColorChanged = { colorEnvelope ->
                                    val color = colorEnvelope.color
                                    tagColor = color
                                }
                            )
                        }
                        WrappingTitle(
                            title = UiText.DynamicString(""),
                            subTitle = UiText.StringResource(R.string.title_recent_colors)
                        ) {
                            ColorTileRow(
                                colors = recentColors,
                                onClick = { controller.selectByColor(it, true) }
                            )
                        }
                    }
                    item {
                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null
                                )
                                Text(text = stringResource(R.string.label_delete))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorTileRow(
    colors: List<Color>,
    onClick: (Color) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        colors.forEach { color ->
            AlphaTile(
                tileOddColor = color,
                tileEvenColor = color,
                modifier = Modifier
                    .size(42.dp)
                    .border(
                        border = BorderStroke(
                            width = if (color == Color.Transparent) 1.dp else 0.dp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onClick(color) }
            )
        }
    }
}