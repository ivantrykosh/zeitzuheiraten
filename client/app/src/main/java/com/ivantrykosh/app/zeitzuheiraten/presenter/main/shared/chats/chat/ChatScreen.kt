package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.chats.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_SYMBOLS_FOR_MESSAGE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messagesViewModel: MessagesViewModel = hiltViewModel(),
    chatId: String?,
    withUserId: String,
    withUserName: String,
    navigateBack: () -> Unit,
) {
    var messageValue by rememberSaveable { mutableStateOf("") }

    val messagesState by messagesViewModel.getMessagesState.collectAsStateWithLifecycle()
    val messages by messagesViewModel.lastMessages.collectAsStateWithLifecycle()
    val getChatIdState by messagesViewModel.getChatByUsersState.collectAsStateWithLifecycle()
    val createMessageState by messagesViewModel.createMessageState.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var isNewChat by rememberSaveable { mutableStateOf(false) }
    var isFirstMessageSent by rememberSaveable { mutableStateOf(false) }
    var firstLoading by rememberSaveable { mutableStateOf(true) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(0) {
        if (chatId != null) {
            messagesViewModel.setChatId(chatId)
            messagesViewModel.getMessages()
        } else {
            messagesViewModel.getChatIdByUsers(withUserId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = withUserName,
                        modifier = Modifier.clickable { /* todo navigate user page by userId */ }
                    )
                },
                windowInsets = WindowInsets(top = 0.dp),
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
                reverseLayout = true
            ) {
                if (messages.isNotEmpty()) {
                    items(messages) { message ->
                        MessageView(message)
                    }
                    if (messagesViewModel.anyNewMessages) {
                        item {
                            Divider(modifier = Modifier.fillMaxWidth())
                            Text(
                                text = stringResource(R.string.load_more),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                                    .clickable {
                                        loaded = false
                                        messagesViewModel.getNewMessages()
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(R.string.no_messages_found),
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageValue,
                    onValueChange = { messageValue = it.take(MAX_SYMBOLS_FOR_MESSAGE) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.type_a_message),
                            fontSize = 16.sp
                        )
                    },
                    maxLines = 4,
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val message = messageValue.trim()
                        if (message.isNotEmpty()) {
                            messagesViewModel.createMessage(message, if (isNewChat) withUserId else null)
                            messageValue = ""
                        }
                    },
                    enabled = messageValue.trim().isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = stringResource(R.string.send_message),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }

    if (!loaded) {
        when {
            messagesState.loading || getChatIdState.loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
            }
            createMessageState.error != null -> {
                loaded = true
                when (createMessageState.error) {
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
            getChatIdState.error != null -> {
                loaded = true
                when (getChatIdState.error) {
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
            messagesState.error != null -> {
                loaded = true
                when (messagesState.error) {
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
            else -> {
                if (isNewChat && !isFirstMessageSent && createMessageState.data != null) {
                    isFirstMessageSent = true
                    messagesViewModel.getChatIdByUsers(withUserId)
                }
                isNewChat = getChatIdState.data == null
                if (!isNewChat && firstLoading) {
                    firstLoading = false
                    messagesViewModel.getMessages()
                }
                if (messagesState.data != null) {
                    loaded = true
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

@Composable
@Preview(showBackground = true)
fun ChatScreenPreview() {
    ChatScreen(
        withUserId = "some id",
        withUserName = "Provider Company",
        navigateBack = {},
        chatId = "chatId",
    )
}