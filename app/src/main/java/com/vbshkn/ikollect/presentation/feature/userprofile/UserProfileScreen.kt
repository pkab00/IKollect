package com.vbshkn.ikollect.presentation.feature.userprofile

import com.vbshkn.ikollect.presentation.feature.userprofile.UserProfileContract.Effect
import com.vbshkn.ikollect.presentation.feature.userprofile.UserProfileContract.Event
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.domain.model.AppUser
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.util.UiText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel,
    onNavigateToAuth: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn = state.user?.email != null

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                Effect.GoToAuthScreen -> onNavigateToAuth()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.onEvent(Event.OnLogOutClick) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoggedIn) {
                state.user?.let { LoggedInContent(it) }
            } else {
                LoggedOutContent(
                    onLoginClick = { viewModel.onEvent(Event.OnLogInClick) }
                )
            }
        }
    }
    if (state.isLoading) {
        LoadingOverlay()
    }
}

@Composable
fun LoggedOutContent(
    onLoginClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.filler_login),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
    Spacer(Modifier.height(16.dp))
    Button(onLoginClick) {
        Text(text = stringResource(R.string.action_login))
    }
}

@Composable
fun LoggedInContent(
    user: AppUser
) {
    val tabs = Tabs.entries.toList()
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ProfileHeader(
            username = user.username,
            uid = user.uid,
            profilePicture = user.profilePictureUrl,
            email = user.email,
            tabs = tabs,
            pagerState = pagerState
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

        }
    }
}

@Composable
private fun ProfileHeader(
    username: String?,
    uid: String?,
    profilePicture: String?,
    email: String?,
    tabs: List<Tabs>,
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .height(160.dp)
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = profilePicture ?: R.drawable.default_avatar,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(75.dp)
        )
        Text(
            text = email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = username ?: "user${uid?.split("-")?.first()}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
    SecondaryTabRow(
        selectedTabIndex = selectedTabIndex.value,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex.value == index,
                unselectedContentColor = MaterialTheme.colorScheme.outline,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(tab.ordinal) }
                },
                text = {
                    Text(
                        text = tab.text.asString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}

private enum class Tabs(val text: UiText) {
    ALBUMS(UiText.StringResource(R.string.screen_title_albums)),
    PHOTOCARDS(UiText.StringResource(R.string.screen_title_photocards)),
    ARTISTS(UiText.StringResource(R.string.screen_title_artists)),
}