package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.feedbacks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    feedbacksViewModel: FeedbacksViewModel = hiltViewModel(),
    postId: String,
    navigateBack: () -> Unit,
    navigateToUser: (String) -> Unit,
) {
    val userIdState by feedbacksViewModel.currentUserIdState.collectAsStateWithLifecycle()
    val feedbacksState by feedbacksViewModel.getFeedbacksState.collectAsStateWithLifecycle()
    val feedbacks by feedbacksViewModel.lastFeedbacks.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = feedbacksState.loading)

    LaunchedEffect(0) {
        feedbacksViewModel.getFeedbacks(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.feedbacks_title)) },
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
                feedbacksViewModel.getFeedbacks(postId)
            },
            indicator = { state, _ ->
                if (state.isRefreshing) {
                    CustomCircularProgressIndicator()
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
                            FeedbackView(
                                feedback = feedback,
                                navigateToUser = {
                                    if (feedback.userId != userIdState.data!!) {
                                        navigateToUser(it)
                                    }
                                }
                            )
                        }
                        if (feedbacksViewModel.anyNewFeedbacks) {
                            item {
                                Divider(modifier = Modifier.fillMaxWidth())
                                Text(
                                    text = stringResource(R.string.load_more),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable {
                                            loaded = false
                                            feedbacksViewModel.getNewFeedbacks(postId)
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    } else if (loaded) {
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

            feedbacksState.data != null -> {
                loaded = true
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
fun FeedbackScreenPreview() {
    FeedbackScreen(
        postId = "",
        navigateBack = {},
        navigateToUser = {}
    )
}