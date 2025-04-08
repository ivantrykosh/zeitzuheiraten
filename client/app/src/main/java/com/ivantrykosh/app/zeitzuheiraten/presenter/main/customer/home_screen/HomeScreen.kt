package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.home_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.home_screen.PostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    navigateToPost: (String) -> Unit,
) {
    val posts by homeScreenViewModel.getPosts.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = posts.loading)

    LaunchedEffect(Unit) {
        homeScreenViewModel.getPostsByCategory("Photography") // todo change category
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { homeScreenViewModel.getPostsByCategory("Photography") },
        indicator = { state, _ ->
            if (state.isRefreshing) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(text = stringResource(R.string.posts)) },
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
                                navigateToPost(post.id)
                            }
                        )
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(R.string.no_posts_found),
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (!loaded) {
        when {
            posts.loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
            }

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