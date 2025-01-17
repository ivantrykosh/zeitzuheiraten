package com.ivantrykosh.app.zeitzuheiraten.presenter.auth.sign_up_screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivantrykosh.app.zeitzuheiraten.R

@Preview(showBackground = false)
@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navigateToVerifyEmailPage: () -> Unit = { }
) {
    val context = LocalContext.current
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var isProvider by remember {
        mutableStateOf(false)
    }

    val createUserState = signUpViewModel.createUserState.collectAsState()
    var loaded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(value = email, onValueChange = { email = it })
        TextField(value = password, onValueChange = { password = it })
        TextField(value = name, onValueChange = { name = it })
        Checkbox(checked = isProvider, onCheckedChange = { isProvider = it })
        Button(onClick = {
            if (
                signUpViewModel.isEmailValid(email) &&
                signUpViewModel.isPasswordValid(password)
                ) {
                signUpViewModel.createUser(email, password, name, isProvider)
            } else {
                Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_LONG).show()
            }
        }) {
            Text(text = stringResource(id = R.string.sign_up_title))
        }
    }

    if (!loaded) {
        when {
            createUserState.value.loading -> {
                Text("Loading...")
            }
            createUserState.value.error.isNotEmpty() -> {
                loaded = true
                Toast.makeText(context, createUserState.value.error, Toast.LENGTH_LONG).show()
            }
            createUserState.value.data != null -> {
                loaded = true
                navigateToVerifyEmailPage()
            }
        }
    }
}