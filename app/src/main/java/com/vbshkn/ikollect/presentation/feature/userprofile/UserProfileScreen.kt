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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vbshkn.ikollect.R
import com.vbshkn.ikollect.presentation.composable.CommonTopBar
import com.vbshkn.ikollect.presentation.composable.LoadingOverlay
import com.vbshkn.ikollect.util.UiText

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
            CommonTopBar(
                title = UiText.StringResource(R.string.screen_title_user_profile),
                actions = {}
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            if (isLoggedIn) {
                LoggedInContent(
                    username = state.user?.username,
                    profilePicture = state.user?.profilePictureUrl,
                    email = state.user?.email,
                    onLogoutClick = { viewModel.onEvent(Event.OnLogOutClick) }
                )
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
    Spacer(Modifier.height(16.dp))
    Button(onLoginClick) {
        Text(text = stringResource(R.string.action_login))
    }
}

@Composable
fun LoggedInContent(
    username: String?,
    profilePicture: String?,
    email: String?,
    onLogoutClick: () -> Unit
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(text = email ?: "")
        }
        item {
            Button(onLogoutClick) {
                Text(text = stringResource(R.string.action_logout))
            }
        }
    }
}