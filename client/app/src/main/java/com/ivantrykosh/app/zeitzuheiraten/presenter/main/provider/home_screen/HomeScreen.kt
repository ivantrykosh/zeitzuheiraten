package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.home_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.PostItem
import com.ivantrykosh.app.zeitzuheiraten.presenter.ui.theme.MediumGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    navigateToAddPostScreen: () -> Unit,
    navigateToEditPostScreen: (String) -> Unit,
) {
    val posts by homeScreenViewModel.getPostsState.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = posts.loading)

    LaunchedEffect(Unit) {
        homeScreenViewModel.getPosts()
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { homeScreenViewModel.getPosts() },
        indicator = { state, _ ->
            if (state.isRefreshing) {
                CustomCircularProgressIndicator()
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.my_posts)) },
                    windowInsets = WindowInsets(top = 0.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (posts.data?.isNotEmpty() == true) {
                        items(posts.data!!) { post ->
                            PostItem(
                                post = post,
                                onPostClick = {
                                    navigateToEditPostScreen(post.id)
                                }
                            )
                        }
                    } else if (loaded) {
                        item {
                            Text(
                                text = stringResource(R.string.you_have_no_posts),
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            if (!homeScreenViewModel.isLimitOfPostsReached()) {
                Box(modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)) {
                    ExtendedFloatingActionButton(
                        onClick = navigateToAddPostScreen,
                        modifier = Modifier.border(2.dp, MediumGray, FloatingActionButtonDefaults.extendedFabShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_post)
                        )
                        Text(
                            text = stringResource(R.string.add_post),
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }

    if (!loaded) {
        when {
            posts.error != null -> {
                loaded = true
                when (posts.error) {
                    is FirebaseNetworkException -> {
                        textInAlertDialog = stringResource(id = R.string.no_internet_connection)
                        showAlertDialog = true
                    }

                    else -> {
                        textInAlertDialog = stringResource(id = R.string.error_occurred)
                        showAlertDialog = true
                    }
                }
                homeScreenViewModel.clearGetPostsState()
            }

            posts.data != null -> {
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
fun HomeScreenPreview() {
    HomeScreen(
        navigateToAddPostScreen = {},
        navigateToEditPostScreen = {}
    )
}