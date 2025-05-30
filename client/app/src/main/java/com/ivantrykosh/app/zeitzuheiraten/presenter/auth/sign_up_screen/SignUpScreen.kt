package com.ivantrykosh.app.zeitzuheiraten.presenter.auth.sign_up_screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.InputField
import com.ivantrykosh.app.zeitzuheiraten.presenter.auth.PasswordInputField
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_SYMBOLS_FOR_USERNAME
import com.ivantrykosh.app.zeitzuheiraten.utils.isEmailValid
import com.ivantrykosh.app.zeitzuheiraten.utils.isFileSizeAppropriate
import com.ivantrykosh.app.zeitzuheiraten.utils.isNameValid
import com.ivantrykosh.app.zeitzuheiraten.utils.isPasswordValid

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navigateToMainPage: (Boolean) -> Unit = { },
    navigateToMainAuthPage: () -> Unit = { }
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var isProvider by rememberSaveable { mutableStateOf(false) }
    var pickedImage by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    val pickUserProfileImage = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            if (isFileSizeAppropriate(uri, contentResolver)) {
                pickedImage = uri
            } else {
                Toast.makeText(context, R.string.file_is_larger_than_5_mb, Toast.LENGTH_LONG).show()
            }
        }
    }

    var emailError by rememberSaveable { mutableStateOf(false) }
    val standardEmailErrorMessage = stringResource(id = R.string.email_invalid)
    var emailErrorMessage by rememberSaveable { mutableStateOf("") }

    var passwordError by rememberSaveable { mutableStateOf(false) }
    val standardPasswordErrorMessage = stringResource(id = R.string.password_invalid)
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }

    var nameError by rememberSaveable { mutableStateOf(false) }
    val standardNameErrorMessage = stringResource(id = R.string.name_invalid)
    var nameErrorMessage by rememberSaveable { mutableStateOf("") }

    val createUserState by signUpViewModel.createUserState.collectAsState()
    var loaded by rememberSaveable { mutableStateOf(false) }

    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.sign_up)) },
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
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    AsyncImage(
                        model = if (pickedImage != Uri.EMPTY) {
                            pickedImage
                        } else {
                            R.drawable.baseline_account_circle_24
                        },
                        contentDescription = stringResource(id = R.string.user_profile_pic),
                        placeholder = painterResource(id = R.drawable.baseline_account_circle_24),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp)
                            .clickable {
                                pickUserProfileImage.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                    )

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

                    InputField(
                        value = name,
                        onValueChange = { name = it.take(MAX_SYMBOLS_FOR_USERNAME) },
                        label = R.string.name,
                        icon = R.drawable.baseline_account_circle_24,
                        iconDescription = R.string.name_icon,
                        onFocusChange = {
                            if (!it.hasFocus && name.isNotEmpty()) {
                                name = name.trim()
                                if (!isNameValid(name)) {
                                    nameError = true
                                    nameErrorMessage = standardNameErrorMessage
                                } else {
                                    nameError = false
                                    nameErrorMessage = ""
                                }
                            }
                        },
                        error = nameError,
                        errorMessage = nameErrorMessage
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Checkbox(checked = isProvider, onCheckedChange = { isProvider = it })
                        Text(
                            text = stringResource(id = R.string.im_provider),
                            fontSize = 16.sp,
                            modifier = Modifier.clickable { isProvider = !isProvider }
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            email = email.trim()
                            password = password.trim()
                            name = name.trim()
                            if (!isEmailValid(email)) {
                                emailError = true
                                emailErrorMessage = standardEmailErrorMessage
                            } else if (!isPasswordValid(password)) {
                                passwordError = true
                                passwordErrorMessage = standardPasswordErrorMessage
                            } else if (!isNameValid(name)) {
                                nameError = true
                                nameErrorMessage = standardNameErrorMessage
                            } else {
                                emailError = false
                                emailErrorMessage = ""
                                passwordError = false
                                passwordErrorMessage = ""
                                nameError = false
                                nameErrorMessage = ""
                                loaded = false
                                signUpViewModel.createUser(
                                    email,
                                    password,
                                    name,
                                    isProvider,
                                    pickedImage
                                )
                            }
                        },
                        shape = RoundedCornerShape(33f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.sign_up_title), fontSize = 16.sp)
                    }
                }
            }

            if (!loaded) {
                when {
                    createUserState.loading -> {
                        CustomCircularProgressIndicator()
                    }

                    createUserState.error != null -> {
                        loaded = true
                        when (createUserState.error) {
                            is FirebaseAuthUserCollisionException -> {
                                emailError = true
                                emailErrorMessage =
                                    stringResource(id = R.string.email_already_in_use)
                            }

                            is FirebaseAuthWeakPasswordException -> {
                                passwordError = true
                                passwordErrorMessage =
                                    stringResource(id = R.string.password_is_too_weak)
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                emailError = true
                                emailErrorMessage =
                                    stringResource(id = R.string.email_or_password_invalid)
                                passwordError = true
                                passwordErrorMessage =
                                    stringResource(id = R.string.email_or_password_invalid)
                            }

                            is FirebaseNetworkException -> {
                                textInAlertDialog =
                                    stringResource(id = R.string.no_internet_connection)
                                showAlertDialog = true
                            }

                            else -> {
                                textInAlertDialog = stringResource(id = R.string.error_occurred)
                                showAlertDialog = true
                            }
                        }
                        signUpViewModel.clearCreateUserState()
                    }

                    createUserState.data != null -> {
                        loaded = true
                        navigateToMainPage(isProvider)
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