package com.ivantrykosh.app.zeitzuheiraten.presenter.auth.sign_in_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.InputField
import com.ivantrykosh.app.zeitzuheiraten.presenter.auth.PasswordInputField
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator
import com.ivantrykosh.app.zeitzuheiraten.presenter.ui.theme.PurpleGrey80
import com.ivantrykosh.app.zeitzuheiraten.utils.isEmailValid
import com.ivantrykosh.app.zeitzuheiraten.utils.isPasswordValid

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel = hiltViewModel(),
    navigateToMainPage: (Boolean) -> Unit = { },
    navigateToMainAuthPage: () -> Unit = { }
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var emailError by rememberSaveable { mutableStateOf(false) }
    val standardEmailErrorMessage = stringResource(id = R.string.email_invalid)
    var emailErrorMessage by rememberSaveable { mutableStateOf("") }

    var passwordError by rememberSaveable { mutableStateOf(false) }
    val standardPasswordErrorMessage = stringResource(id = R.string.password_invalid)
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }

    val signInState by signInViewModel.signInState.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }

    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.sign_in)) },
                navigationIcon = {
                    IconButton(
                        onClick = { navigateToMainAuthPage() }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = stringResource(id =R.string.return_to_auth))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier.align(Alignment.Center),
                colors = CardDefaults.cardColors(
                    containerColor = PurpleGrey80
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    InputField(
                        value = email,
                        onValueChange = { email = it.take(320) },
                        label = R.string.email,
                        icon = R.drawable.baseline_email_24,
                        iconDescription = R.string.email_icon,
                        onFocusChange = {
                            if (!it.hasFocus && email.isNotEmpty()) {
                                email = email.trim()
                                if (!isEmailValid(email)) {
                                    emailError = true
                                    emailErrorMessage = standardEmailErrorMessage
                                } else {
                                    emailError = false
                                    emailErrorMessage = ""
                                }
                            }
                        },
                        error = emailError,
                        errorMessage = emailErrorMessage
                    )

                    PasswordInputField(
                        value = password,
                        onValueChange = { password = it.take(64) },
                        onFocusChange = {
                            if (!it.hasFocus) {
                                password = password.trim()
                                if (!isPasswordValid(password) && password.isNotEmpty()) {
                                    passwordError = true
                                    passwordErrorMessage = standardPasswordErrorMessage
                                } else {
                                    passwordError = false
                                    passwordErrorMessage = ""
                                }
                            }
                        },
                        error = passwordError,
                        errorMessage = passwordErrorMessage
                    )

                    FilledTonalButton(
                        onClick = {
                            email = email.trim()
                            password = password.trim()
                            if (!isEmailValid(email)) {
                                emailError = true
                                emailErrorMessage = standardEmailErrorMessage
                            } else if (!isPasswordValid(password)) {
                                passwordError = true
                                passwordErrorMessage = standardPasswordErrorMessage
                            } else {
                                emailError = false
                                emailErrorMessage = ""
                                passwordError = false
                                passwordErrorMessage = ""
                                loaded = false
                                signInViewModel.signIn(email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.sign_in_title), fontSize = 16.sp)
                    }
                }
            }


            if (!loaded) {
                when {
                    signInState.loading -> {
                        CustomCircularProgressIndicator()
                    }
                    signInState.error != null -> {
                        loaded = true
                        when (signInState.error) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                emailError = true
                                emailErrorMessage = stringResource(id = R.string.email_or_password_invalid)
                                passwordError = true
                                passwordErrorMessage = stringResource(id = R.string.email_or_password_invalid)
                            }
                            is FirebaseNetworkException -> {
                                textInAlertDialog = stringResource(id = R.string.no_internet_connection)
                                showAlertDialog = true
                            }
                            else -> {
                                textInAlertDialog = stringResource(id = R.string.error_occurred)
                                showAlertDialog = true
                            }
                        }
                    }
                    signInState.data != null -> {
                        loaded = true
                        navigateToMainPage(signInState.data!!)
                    }
                }
            }
        }
    }
    
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable { showAlertDialog = false }) },
            title = { Text(text = stringResource(id = R.string.error)) },
            text = { Text(text = textInAlertDialog) }
        )
    }
}