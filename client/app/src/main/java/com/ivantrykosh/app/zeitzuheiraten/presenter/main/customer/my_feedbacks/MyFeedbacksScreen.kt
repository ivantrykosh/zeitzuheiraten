package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_feedbacks

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFeedbacksScreen(
    myFeedbacksViewModel: MyFeedbacksViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val feedbacksState by myFeedbacksViewModel.getFeedbacksState.collectAsStateWithLifecycle()
    val deleteFeedbackState by myFeedbacksViewModel.deleteFeedbackState.collectAsStateWithLifecycle()
    val feedbacks by myFeedbacksViewModel.lastFeedbacks.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var isDeleteFeedbackDialogShowed by rememberSaveable { mutableStateOf(false) }
    var pickedFeedbackId by rememberSaveable { mutableStateOf("") }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = feedbacksState.loading || deleteFeedbackState.loading)

    LaunchedEffect(0) {
        myFeedbacksViewModel.getFeedbacks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.my_feedbacks)) },
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
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                loaded = false
                myFeedbacksViewModel.getFeedbacks()
            },
            indicator = { state, _ ->
                if (state.isRefreshing) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
                }
            },
            modifier = Modifier.padding(it).fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (feedbacks.isNotEmpty()) {
                        items(feedbacks) { feedback ->
                            MyFeedbackView(
                                feedback = feedback,
                                onDeleteClicked = {
                                    loaded = false
                                    isDeleteFeedbackDialogShowed = true
                                    pickedFeedbackId = it
                                }
                            )
                        }
                        if (myFeedbacksViewModel.anyNewFeedbacks) {
                            item {
                                Divider(modifier = Modifier.fillMaxWidth())
                                Text(
                                    text = stringResource(R.string.load_more),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable {
                                            loaded = false
                                            myFeedbacksViewModel.getNewFeedbacks()
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = stringResource(R.string.no_feedbacks_found),
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (!loaded) {
        when {
            feedbacksState.loading || deleteFeedbackState.loading -> { }
            deleteFeedbackState.error != null -> {
                loaded = true
                when (deleteFeedbackState.error) {
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
            feedbacksState.error != null -> {
                loaded = true
                when (feedbacksState.error) {
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
                if (deleteFeedbackState.data != null) {
                    Toast.makeText(LocalContext.current, R.string.feedback_was_deleted, Toast.LENGTH_LONG).show()
                    myFeedbacksViewModel.clearDeleteFeedbackState()
                    myFeedbacksViewModel.getFeedbacks()
                }
                if (feedbacksState.data != null) {
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

    if (isDeleteFeedbackDialogShowed) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable {
                loaded = false
                isDeleteFeedbackDialogShowed = false
                myFeedbacksViewModel.deleteFeedback(pickedFeedbackId)
            }) },
            dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable {
                isDeleteFeedbackDialogShowed = false
            }) },
            title = { Text(text = stringResource(id = R.string.delete_feedback)) },
            text = { Text(text = stringResource(id = R.string.delete_feedback_question)) }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun MyFeedbacksScreenPreview() {
    MyFeedbacksScreen(
        navigateBack = {}
    )
}