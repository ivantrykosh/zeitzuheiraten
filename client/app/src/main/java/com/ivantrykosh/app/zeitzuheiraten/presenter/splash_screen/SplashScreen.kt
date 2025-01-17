package com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivantrykosh.app.zeitzuheiraten.R
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel = hiltViewModel(),
    navigateToAuthPage: () -> Unit = { },
    navigateToMainPage: () -> Unit = { }
) {
    val isUserLoggedInAndIsEmailVerified by splashViewModel.isUserLoggedInAndIsEmailVerifiedState.collectAsStateWithLifecycle()
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = 0) {
        delay(1000)
        splashViewModel.isUserLoggedIn()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.wrapContentSize().align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // TODO change app icon
                contentDescription = stringResource(id = R.string.app_icon),
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier.padding(8.dp)
            )
        }
    }

    // Stop after data is loaded or error occurred
    if (!loaded) {
        when {
            isUserLoggedInAndIsEmailVerified.error.isNotEmpty() -> {
                loaded = true
                Text(text = isUserLoggedInAndIsEmailVerified.error)
            }

            isUserLoggedInAndIsEmailVerified.data != null -> {
                loaded = true
                if (isUserLoggedInAndIsEmailVerified.data!!) {
                    navigateToMainPage()
                } else {
                    navigateToAuthPage()
                }
            }
        }
    }
}