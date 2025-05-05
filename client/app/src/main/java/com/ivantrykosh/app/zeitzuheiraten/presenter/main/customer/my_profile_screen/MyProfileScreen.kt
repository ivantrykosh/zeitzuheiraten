package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_profile_screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.presenter.InputField
import com.ivantrykosh.app.zeitzuheiraten.presenter.auth.PasswordInputField
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.ButtonView
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_SYMBOLS_FOR_USERNAME
import com.ivantrykosh.app.zeitzuheiraten.utils.isFileSizeAppropriate
import com.ivantrykosh.app.zeitzuheiraten.utils.isNameValid
import com.ivantrykosh.app.zeitzuheiraten.utils.isPasswordValid

@Preview(showBackground = true)
@Composable
fun MyProfileScreen(
    myProfileViewModel: MyProfileViewModel = hiltViewModel(),
    onSignOut: () -> Unit = { },
    navigateToMyFeedbacks: () -> Unit = { },
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val getCurrentUserState by myProfileViewModel.getCurrentUserState.collectAsState()
    var currentUser by rememberSaveable { mutableStateOf(User()) }
    LaunchedEffect(key1 = 0) {
        myProfileViewModel.getCurrentUser()
    }

    var signOutDone by rememberSaveable { mutableStateOf(false) }
    val signOutState by myProfileViewModel.signOutState.collectAsState()

    var updateUserDone by rememberSaveable { mutableStateOf(false) }
    val updateUserState by myProfileViewModel.updateUserState.collectAsState()

    var updateProfileImageDone by rememberSaveable { mutableStateOf(false) }
    val updateProfileImageState by myProfileViewModel.updateUserProfileImageState.collectAsState()

    var deleteUserDone by rememberSaveable { mutableStateOf(false) }
    val deleteUserState by myProfileViewModel.deleteUserState.collectAsState()

    var reAuthenticateDone by rememberSaveable { mutableStateOf(false) }
    val reAuthenticateState by myProfileViewModel.reAuthenticateState.collectAsState()

    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var textInErrorDialog by rememberSaveable { mutableStateOf("") }

    var showInputDialog by rememberSaveable { mutableStateOf(false) }

    var showConfirmDeleteDialog by rememberSaveable { mutableStateOf(false) }

    val pickUserProfileImage = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            if (isFileSizeAppropriate(uri, contentResolver)) {
                updateProfileImageDone = false
                myProfileViewModel.updateUserProfileImage(uri)
            } else {
                Toast.makeText(context, R.string.file_is_larger_than_5_mb, Toast.LENGTH_LONG).show()
            }
        }
    }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    val standardPasswordErrorMessage = stringResource(id = R.string.password_invalid)
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = currentUser.imageUrl.ifEmpty {
                    R.drawable.baseline_account_circle_24
                },
                contentDescription = stringResource(id = R.string.user_profile_pic),
                placeholder = painterResource(id = R.drawable.baseline_account_circle_24),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            )
            Text(text = currentUser.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = currentUser.email, fontSize = 16.sp)

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                ButtonView(
                    onClick = {
                        navigateToMyFeedbacks()
                    },
                    iconRes = R.drawable.baseline_feedback_24,
                    title = R.string.my_feedbacks
                )
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp))
                ButtonView(
                    onClick = {
                        pickUserProfileImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    iconRes = R.drawable.baseline_change_circle_24,
                    title = R.string.change_profile_image
                )
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp))
                ButtonView(
                    onClick = {
                        updateProfileImageDone = false
                        myProfileViewModel.updateUserProfileImage(Uri.EMPTY)
                    },
                    iconRes = R.drawable.baseline_delete_24,
                    title = R.string.delete_profile_image
                )
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp))
                ButtonView(
                    onClick = {
                        showInputDialog = true
                    },
                    iconRes = R.drawable.baseline_edit_24,
                    title = R.string.edit_name
                )
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp))
                ButtonView(
                    onClick = {
                        showConfirmDeleteDialog = true
                    },
                    iconRes = R.drawable.baseline_delete_forever_24,
                    title = R.string.delete_account
                )
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp))
                ButtonView(
                    onClick = {
                        signOutDone = false
                        myProfileViewModel.signOut()
                    },
                    iconRes = R.drawable.baseline_logout_24,
                    title = R.string.sign_out
                )
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp))
            }
        }

        when {
            getCurrentUserState.loading -> {
                CustomCircularProgressIndicator()
            }
            getCurrentUserState.error != null -> {
                when (getCurrentUserState.error) {
                    is FirebaseNetworkException -> {
                        textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                    }
                    else -> {
                        textInErrorDialog = stringResource(id = R.string.error_occurred)
                    }
                }
                showErrorDialog = true
            }
            getCurrentUserState.data != null -> {
                currentUser = getCurrentUserState.data!!
            }
        }
        if (!signOutDone) {
            when {
                signOutState.loading -> {
                    CustomCircularProgressIndicator()
                }
                signOutState.error != null -> {
                    signOutDone = true
                    when (signOutState.error) {
                        is FirebaseNetworkException -> {
                            textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        }
                        else -> {
                            textInErrorDialog = stringResource(id = R.string.error_occurred)
                        }
                    }
                    showErrorDialog = true
                }
                signOutState.data != null -> {
                    signOutDone = true
                    onSignOut()
                }
            }
        }
        if (!updateProfileImageDone) {
            when {
                updateProfileImageState.loading -> {
                    CustomCircularProgressIndicator()
                }
                updateProfileImageState.error != null -> {
                    updateProfileImageDone = true
                    when (updateProfileImageState.error) {
                        is FirebaseNetworkException -> {
                            textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        }
                        else -> {
                            textInErrorDialog = stringResource(id = R.string.error_occurred)
                        }
                    }
                    showErrorDialog = true
                }
                updateProfileImageState.data != null -> {
                    updateProfileImageDone = true
                    myProfileViewModel.getCurrentUser()
                }
            }
        }
        if (!updateUserDone) {
            when {
                updateUserState.loading -> {
                    CustomCircularProgressIndicator()
                }
                updateUserState.error != null -> {
                    updateUserDone = true
                    when (updateUserState.error) {
                        is FirebaseNetworkException -> {
                            textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        }
                        else -> {
                            textInErrorDialog = stringResource(id = R.string.error_occurred)
                        }
                    }
                    showErrorDialog = true
                }
                updateUserState.data != null -> {
                    updateUserDone = true
                    myProfileViewModel.getCurrentUser()
                }
            }
        }
        if (!deleteUserDone) {
            when {
                deleteUserState.loading -> {
                    CustomCircularProgressIndicator()
                }
                deleteUserState.error != null -> {
                    deleteUserDone = true
                    when (deleteUserState.error) {
                        is FirebaseNetworkException -> {
                            textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        }
                        else -> {
                            textInErrorDialog = stringResource(id = R.string.error_occurred)
                        }
                    }
                    showErrorDialog = true
                }
                deleteUserState.data != null -> {
                    deleteUserDone = true
                    onSignOut()
                }
            }
        }
        if (!reAuthenticateDone) {
            when {
                reAuthenticateState.loading -> {
                    CustomCircularProgressIndicator()
                }
                reAuthenticateState.error != null -> {
                    reAuthenticateDone = true
                    when (reAuthenticateState.error) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            passwordError = true
                            passwordErrorMessage = stringResource(id = R.string.password_invalid)
                        }
                        is FirebaseNetworkException -> {
                            textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                            showErrorDialog = true
                        }
                        else -> {
                            textInErrorDialog = stringResource(id = R.string.error_occurred)
                            showErrorDialog = true
                        }
                    }
                }
                reAuthenticateState.data != null -> {
                    reAuthenticateDone = true
                    deleteUserDone = false
                    myProfileViewModel.deleteUser()
                    showConfirmDeleteDialog = false
                }
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable { showErrorDialog = false }) },
            title = { Text(text = stringResource(id = R.string.error)) },
            text = { Text(text = textInErrorDialog) }
        )
    }


    var name by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf(false) }
    val standardNameErrorMessage = stringResource(id = R.string.name_invalid)
    var nameErrorMessage by rememberSaveable { mutableStateOf("") }

    if (showInputDialog) {
        AlertDialog(
            onDismissRequest = { showInputDialog = false },
            confirmButton = {
                Text(
                    text = stringResource(id = R.string.ok_title),
                    modifier = Modifier.clickable {
                        name = name.trim()
                        if (!isNameValid(name)) {
                            nameError = true
                            nameErrorMessage = standardNameErrorMessage
                        } else {
                            nameError = false
                            nameErrorMessage = ""
                            updateUserDone = false
                            myProfileViewModel.updateUser(currentUser.copy(name = name))
                            showInputDialog = false
                        }
                    }
                )
            },
            dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable { showInputDialog = false }) },
            title = { Text(text = stringResource(id = R.string.edit_name)) },
            text = {
                InputField(
                    value = name,
                    onValueChange = { name = it.take(MAX_SYMBOLS_FOR_USERNAME) },
                    label = R.string.new_name,
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
            }
        )
    }

    if (showConfirmDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteDialog = false },
            confirmButton = {
                Text(
                    text = stringResource(id = R.string.ok_title),
                    modifier = Modifier.clickable {
                        password = password.trim()
                        if (!isPasswordValid(password) && password.isNotEmpty()) {
                            passwordError = true
                            passwordErrorMessage = standardPasswordErrorMessage
                        } else {
                            passwordError = false
                            passwordErrorMessage = ""
                            reAuthenticateDone = false
                            myProfileViewModel.reAuthenticate(password)
                        }
                    }
                )
            },
            dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable { showConfirmDeleteDialog = false }) },
            title = { Text(text = stringResource(id = R.string.delete_account)) },
            text = {
                Column {
                    Text(text = stringResource(id = R.string.delete_account_question))
                    PasswordInputField(
                        value = password,
                        onValueChange = { password = it },
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
                }
            }
        )
    }
}