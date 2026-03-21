package com.vbshkn.ikollect.presentation.feature.addalbum

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.vbshkn.ikollect.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddDetailsScreen(
    viewModel: AddAlbumViewModel,
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(top = 16.dp)
    ) {
        item {
            ItemWrapper(stringResource(R.string.add_details_title_image)) {
                SelectImageItem(
                    imageUrl = uiState.versionCandidate!!.coverImage,
                    onEvent = viewModel::onEvent
                )
            }
        }
        item {
            ItemWrapper("Номер KOMCA", true) {
                val text = remember { mutableStateOf("") }
                KomcaNumberField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    onScanClick = {}
                )
            }
        }
    }
}

@Composable
fun KomcaNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    onScanClick: () -> Unit
) {
    val isError = value.isNotEmpty() && value.length < 8

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                onValueChange(newValue)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues()),
        label = { Text("KOMCA") },
        placeholder = { Text("12345678") },
        prefix = { Text("№ ") },
        trailingIcon = {
            IconButton(onClick = onScanClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_scanner),
                    contentDescription = null
                )
            }
        },
        supportingText = {
            if (isError) {
                Text("Слишком короткий номер", color = MaterialTheme.colorScheme.error)
            } else {
                Text("8–12 цифр с голографической наклейки")
            }
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ItemWrapper(
    title: String,
    showHint: Boolean = false,
    onHint: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.size(24.dp)) {
                if (showHint) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        modifier = Modifier.clickable { onHint() }
                    )
                }
            }
        }
        content()
    }
}

@Composable
private fun SelectImageItem(
    imageUrl: String?,
    onEvent: (AddAlbumContract.Event) -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            CoverImage(imageUrl)
            Text(
                text = stringResource(
                    if (imageUrl != null) R.string.add_details_caption_has_cover
                    else R.string.add_details_caption_no_cover
                ),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { onEvent(AddAlbumContract.Event.OnSelectPicture) }) {
                    Text(text = stringResource(R.string.add_details_select_picture))
                }
                Button(onClick = { onEvent(AddAlbumContract.Event.OnTakePicture) }) {
                    Text(text = stringResource(R.string.add_details_take_picture))
                }
            }
        }
    }
}

@Composable
private fun CoverImage(imageUrl: String?) {
    if (imageUrl == null) {
        Box(
            Modifier
                .width(100.dp)
                .height(135.dp)
                .background(Color.LightGray)
        )
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .height(135.dp)
        )
    }
}