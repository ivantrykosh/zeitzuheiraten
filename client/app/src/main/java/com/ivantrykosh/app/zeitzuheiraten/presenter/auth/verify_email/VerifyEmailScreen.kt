package com.ivantrykosh.app.zeitzuheiraten.presenter.auth.verify_email

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun VerifyEmailScreen(verifyEmailViewModel: VerifyEmailViewModel = hiltViewModel()) {
    val sendVerificationEmailState by verifyEmailViewModel.sendVerificationEmailState.collectAsStateWithLifecycle()
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = 0) {
        verifyEmailViewModel.sendVerificationEmail()
    }

    if (!loaded) {
        when {
            sendVerificationEmailState.loading -> {
                Text(text = "Sending letter")
            }

            sendVerificationEmailState.error.isNotEmpty() -> {
                loaded = true
                Text(text = sendVerificationEmailState.error)
            }

            sendVerificationEmailState.data != null -> {
                loaded = true
                Text(text = "Confirmation email was sent")
            }
        }
    }
}