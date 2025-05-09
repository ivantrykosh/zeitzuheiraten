package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.User
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_SYMBOLS_FOR_REPORT_DESCRIPTION
import kotlin.text.ifEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    userId: String,
    onOpenChatClicked: (String, String) -> Unit,
    navigateBack: () -> Unit,
) {
    val getCurrentUser by profileViewModel.getCurrentUser.collectAsStateWithLifecycle()
    val getUserState by profileViewModel.getUserByIdState.collectAsStateWithLifecycle()
    var user by rememberSaveable { mutableStateOf<User?>(null) }
    val createReportState by profileViewModel.createReportState.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(true) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var textInErrorDialog by rememberSaveable { mutableStateOf("") }
    var navigateBackAfterError by rememberSaveable { mutableStateOf(false) }
    var reportUserDialogOpened by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(0) {
        loaded = false
        profileViewModel.getCurrentUser()
        profileViewModel.getUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.user)) },
                windowInsets = WindowInsets(top = 0.dp),
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
            )
        }
    ) {
        if (loaded && user != null) {
            Column(
                modifier = Modifier.padding(it).fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = user!!.imageUrl.ifEmpty {
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
                Text(
                    text = user!!.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                if (!(!getCurrentUser.data!!.isProvider && !user!!.isProvider)) {
                    Button(
                        onClick = {
                            onOpenChatClicked(userId, user!!.name)
                        },
                        modifier = Modifier.fillMaxWidth(0.5f).align(Alignment.CenterHorizontally),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(2.dp, color = Color.Black)
                    ) {
                        Text(text = stringResource(R.string.open_chat).uppercase())
                    }
                }
                Button(
                    onClick = {
                        reportUserDialogOpened = true
                    },
                    modifier = Modifier.fillMaxWidth(0.5f).align(Alignment.CenterHorizontally),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(2.dp, color = Color.Black)
                ) {
                    Text(text = stringResource(R.string.report_user).uppercase())
                }
            }
        }
    }

    if (!loaded) {
        when {
            getCurrentUser.loading || getUserState.loading || createReportState.loading -> {
                CustomCircularProgressIndicator()
            }
            getCurrentUser.error != null -> {
                loaded = true
                when (getCurrentUser.error) {
                    is FirebaseNetworkException -> {
                        textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        showErrorDialog = true
                    }

                    else -> {
                        textInErrorDialog = stringResource(id = R.string.error_occurred)
                        showErrorDialog = true
                    }
                }
                profileViewModel.clearGetCurrentUserState()
            }
            getUserState.error != null -> {
                loaded = true
                when (getUserState.error) {
                    is NullPointerException -> {
                        textInErrorDialog = stringResource(R.string.user_doesnt_exist)
                        showErrorDialog = true
                        navigateBackAfterError = true
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
                profileViewModel.clearGetUserByIdState()
            }
            createReportState.error != null -> {
                loaded = true
                when (createReportState.error) {
                    is FirebaseNetworkException -> {
                        textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        showErrorDialog = true
                    }

                    else -> {
                        textInErrorDialog = stringResource(id = R.string.error_occurred)
                        showErrorDialog = true
                    }
                }
                profileViewModel.clearCreateReportState()
            }
            else -> {
                if (createReportState.data != null) {
                    loaded = true
                    Toast.makeText(LocalContext.current, R.string.report_about_user_was_sent, Toast.LENGTH_LONG).show()
                }
                if (getUserState.data != null && getCurrentUser.data != null) {
                    loaded = true
                    user = getUserState.data
                }
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable {
                showErrorDialog = false
                if (navigateBackAfterError) {
                    navigateBack()
                }
            }) },
            title = { Text(text = stringResource(id = R.string.error)) },
            text = { Text(text = textInErrorDialog) }
        )
    }

    if (reportUserDialogOpened) {
        ReportUserDialog(
            onOkPressed = {
                loaded = false
                reportUserDialogOpened = false
                profileViewModel.createReport(getUserState.data!!.id, it)
            },
            onDismiss = { reportUserDialogOpened = false }
        )
    }
}

@Composable
fun ReportUserDialog(
    onOkPressed: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var description by rememberSaveable { mutableStateOf("") }
    var isDescriptionError by rememberSaveable { mutableStateOf(false) }
    val descriptionErrorMessage = stringResource(R.string.you_need_to_add_details)

    AlertDialog(
        onDismissRequest = {},
        confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable {
            description = description.trim()
            if (description.isEmpty()) {
                isDescriptionError = true
            } else {
                onOkPressed(description)
            }
        }) },
        dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable {
            onDismiss()
        }) },
        title = { Text(text = stringResource(id = R.string.report_user)) },
        text = {
            TextField(
                value = description,
                onValueChange = {
                    description = it.take(MAX_SYMBOLS_FOR_REPORT_DESCRIPTION)
                    if (description.isNotBlank()) {
                        isDescriptionError = false
                    }
                },
                minLines = 5,
                maxLines = 5,
                label = {
                    Text(text = stringResource(R.string.description))
                },
                modifier = Modifier.fillMaxWidth(),
                isError = isDescriptionError,
                supportingText = {
                    if (isDescriptionError) {
                        Text(descriptionErrorMessage)
                    }
                }
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    ProfileScreen(
        userId = "id",
        navigateBack = {},
        onOpenChatClicked = { _, _ -> }
    )
}