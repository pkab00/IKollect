package com.vbshkn.ikollect.presentation.feature.artists.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarState
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun ArtistProfileScreen(
    viewModel: ArtistProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val state = rememberCollapsingToolbarScaffoldState()
    val profile = uiState.profileData

    val expandedHeight = 300.dp
    val collapsedHeight = 56.dp

    CollapsingToolbarScaffold(
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbarModifier = Modifier.background(MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxSize(),
        toolbar = {
            ArtistCollapsingToolbar(
                toolbarState = state.toolbarState,
                title = profile?.artist?.name ?: "",
                imageUrl = profile?.artist?.profileImage,
                onBackClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(expandedHeight)
                    .parallax(0.5f),
                textModifier = Modifier
                    .road(
                        whenCollapsed = Alignment.CenterStart,
                        whenExpanded = Alignment.BottomStart
                    )
                    .padding(start = 56.dp, end = 16.dp, top = collapsedHeight, bottom = 16.dp),
                backModifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(collapsedHeight)
                    .pin()
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(55) {
                Row(Modifier.fillMaxWidth()) {
                    Text("Item $it")
                }
            }
        }
    }
    if (uiState.isLoading) {
        LoadingOverlay()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistCollapsingToolbar(
    toolbarState: CollapsingToolbarState,
    title: String,
    imageUrl: String?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    backModifier: Modifier = Modifier,
) {
    val textSize = (20f + (12f * toolbarState.progress)).sp


    // Основной фон с картинкой
    Box(
        modifier = modifier
    ) {
        // Фотография артиста
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Градиент
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        ),
                        startY = 300f
                    )
                )
        )

        // Дополнительное затемнение при сворачивании
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f - toolbarState.progress))
        )
    }

    // 3. Анимированный заголовок (Имя)
    Text(
        text = title,
        color = if (toolbarState.progress < 0.3f)
            MaterialTheme.colorScheme.onSurfaceVariant
        else Color.White,
        fontSize = textSize,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = textModifier
    )

    // 4. Кнопка "Назад" (закреплена всегда сверху)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = backModifier
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = if (toolbarState.progress < 0.3f) MaterialTheme.colorScheme.onSurfaceVariant
                else Color.White
            )
        }
    }
}