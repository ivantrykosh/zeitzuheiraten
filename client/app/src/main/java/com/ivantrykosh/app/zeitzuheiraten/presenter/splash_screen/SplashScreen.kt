package com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val isUserLoggedIn by splashViewModel.isUserLoggedInState.collectAsStateWithLifecycle()
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = 0) {
        delay(1000)
        splashViewModel.isUserLoggedIn()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // TODO change app icon
                contentDescription = stringResource(id = R.string.app_icon),
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    // Stop after data is loaded or error occurred
    if (!loaded) {
        when {
            isUserLoggedIn.error != null -> {
                loaded = true
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = { Text(text = stringResource(id = R.string.ok_title)) },
                    title = { Text(text = stringResource(id = R.string.error)) },
                    text = { Text(text = isUserLoggedIn.error?.message ?: stringResource(id = R.string.error_occurred)) }
                )
            }

            isUserLoggedIn.data != null -> {
                loaded = true
                if (isUserLoggedIn.data!!) {
                    navigateToMainPage()
                } else {
                    navigateToAuthPage()
                }
            }
        }
    }
}