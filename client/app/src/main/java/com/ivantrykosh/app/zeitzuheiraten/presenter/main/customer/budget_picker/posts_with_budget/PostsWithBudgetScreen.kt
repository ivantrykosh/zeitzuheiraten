package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.budget_picker.posts_with_budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.FilterItemDropdown
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.budget_picker.BudgetPickerViewModel
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.home_screen.PostItem
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsWithBudgetScreen(
    budgetPickerViewModel: BudgetPickerViewModel,
    navigateBack: () -> Unit,
    navigateToPost: (String) -> Unit,
) {
    val postsState by budgetPickerViewModel.getPosts.collectAsStateWithLifecycle()
    val posts by budgetPickerViewModel.lastPosts.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }

    val categories = budgetPickerViewModel.categories
    var currentCategory by rememberSaveable { mutableStateOf(categories[0]) }
    var postsOrderType by rememberSaveable { mutableStateOf(PostsOrderType.BY_CATEGORY) }
    var showSortByDialog by rememberSaveable { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = postsState.loading)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(0) {
        budgetPickerViewModel.updateBudgetAndGetPosts(currentCategory, postsOrderType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.posts_with_budget)) },
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
                actions = {
                    IconButton(
                        onClick = { showSortByDialog = true }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_sort_by_alpha_24),
                            contentDescription = stringResource(R.string.sort_posts)
                        )
                    }
                }
            )
        }
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                loaded = false
                budgetPickerViewModel.getPosts(currentCategory, postsOrderType)
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
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        val isCurrent = currentCategory == category
                        TextButton(
                            onClick = {
                                currentCategory = category
                                budgetPickerViewModel.getPosts(currentCategory, postsOrderType)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (isCurrent) Color.LightGray else Color.White,
                                contentColor = if (isCurrent) Color.Black else Color.DarkGray
                            )
                        ) {
                            Text(
                                text = category,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (posts.isNotEmpty()) {
                        items(posts) { post ->
                            PostItem(
                                post = post,
                                onPostClick = {
                                    navigateToPost(post.id)
                                }
                            )
                        }
                        if (budgetPickerViewModel.anyNewPosts) {
                            item {
                                Divider(modifier = Modifier.fillMaxWidth())
                                Text(
                                    text = stringResource(R.string.load_more),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable {
                                            loaded = false
                                            budgetPickerViewModel.getNewPosts(currentCategory, postsOrderType)
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    } else if (loaded) {
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
    }

    if (!loaded) {
        when {
            postsState.error != null -> {
                loaded = true
                when (postsState.error) {
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

            postsState.data != null -> {
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
    if (showSortByDialog) {
        val onDismiss = {
            showSortByDialog = false
            postsOrderType = budgetPickerViewModel.lastPostsOrderType
        }
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sort_posts),
                        style = MaterialTheme.typography.titleLarge
                    )

                    FilterItemDropdown(
                        currentValue = postsOrderType.getString(context),
                        onValueChange = { postsOrderType = PostsOrderType.getObjectByString(context, it)},
                        label = stringResource(R.string.order),
                        values = PostsOrderType.toStringList(context),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                showSortByDialog = false
                                loaded = false
                                coroutineScope.launch {
                                    lazyListState.scrollToItem(0)
                                }
                                budgetPickerViewModel.getPosts(currentCategory, postsOrderType)
                            }
                        ) {
                            Text(text = stringResource(R.string.ok_title))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PostsWithBudgetScreenPreview() {
    PostsWithBudgetScreen(
        budgetPickerViewModel = hiltViewModel(),
        navigateBack = {},
        navigateToPost = {}
    )
}