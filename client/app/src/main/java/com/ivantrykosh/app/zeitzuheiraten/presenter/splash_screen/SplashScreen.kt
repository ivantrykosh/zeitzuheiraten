package com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivantrykosh.app.zeitzuheiraten.BuildConfig
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel = hiltViewModel(),
    navigateToAuthPage: () -> Unit = { },
    navigateToMainPage: (Boolean) -> Unit = { }
) {
    val isUserLoggedIn by splashViewModel.isUserLoggedInState.collectAsStateWithLifecycle()
    val getCurrentVersion by splashViewModel.getLatestAppVersionState.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var showUpdateDialog by rememberSaveable { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(key1 = 0) {
        delay(1000)
        splashViewModel.isUserLoggedIn()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_icon),
                modifier = Modifier.padding(8.dp).scale(3f)
            )
        }
    }

    val navigateNext = {
        if (isUserLoggedIn.data!!) {
            navigateToMainPage(splashViewModel.isUserProvider)
        } else {
            navigateToAuthPage()
        }
    }

    // Stop after data is loaded or error occurred
    if (!loaded) {
        when {
            isUserLoggedIn.error != null -> {
                loaded = true
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = { },
                    title = { Text(text = stringResource(id = R.string.error)) },
                    text = { Text(text = isUserLoggedIn.error?.message ?: stringResource(id = R.string.error_occurred)) }
                )
                splashViewModel.clearIsUserLoggedInState()
            }
            getCurrentVersion.error != null -> {
                loaded = true
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = { },
                    title = { Text(text = stringResource(id = R.string.error)) },
                    text = { Text(text = getCurrentVersion.error?.message ?: stringResource(id = R.string.error_occurred)) }
                )
                splashViewModel.clearGetLatestAppVersionState()
            }

            isUserLoggedIn.data != null && !getCurrentVersion.loading -> {
                loaded = true
                val currentVersion = BuildConfig.VERSION_NAME
                val latestVersion = getCurrentVersion.data
                if (latestVersion != null && splashViewModel.inNewerVersion(currentVersion, latestVersion)) {
                    showUpdateDialog = true
                } else {
                    navigateNext()
                }
            }
        }
    }

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUpdateDialog = false
                        uriHandler.openUri(Constants.LATEST_APP_RELEASE_URL)
                        navigateNext()
                    }
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showUpdateDialog = false
                        navigateNext()
                    }
                ) {
                    Text(stringResource(R.string.later))
                }
            },
            title = {
                Text(stringResource(R.string.there_is_update))
            },
            text = {
                Text(stringResource(R.string.do_you_want_to_update))
            }
        )
    }
}