package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.home_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.FilterItemDropdown
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.FilterItemInputNumber
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.PostItem
import com.ivantrykosh.app.zeitzuheiraten.utils.PostsOrderType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    navigateToPost: (String) -> Unit,
) {
    val postsState by homeScreenViewModel.getPosts.collectAsStateWithLifecycle()
    val posts by homeScreenViewModel.lastPosts.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }

    var categoryValue by rememberSaveable { mutableStateOf("") }
    val categories = stringArrayResource(R.array.categories)
    var cityValue by rememberSaveable { mutableStateOf("") }
    val cities = stringArrayResource(R.array.cities)
    var maxPriceValue by rememberSaveable { mutableStateOf("") }
    var postsOrderType by rememberSaveable { mutableStateOf(PostsOrderType.BY_CATEGORY) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    var showFiltersDialog by rememberSaveable { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = postsState.loading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            loaded = false
            homeScreenViewModel.getPostsByFilters(categoryValue, cityValue, maxPriceValue.toIntOrNull(), postsOrderType, reset = true)
        },
        indicator = { state, _ ->
            if (state.isRefreshing) {
                CustomCircularProgressIndicator()
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(text = stringResource(R.string.posts)) },
                windowInsets = WindowInsets(top = 0.dp),
                actions = {
                    IconButton(
                        onClick = { showFiltersDialog = true }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_filter_alt_24),
                            contentDescription = stringResource(R.string.filter_posts)
                        )
                    }
                }
            )
            LazyColumn(
                state = lazyListState,
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
                    if (homeScreenViewModel.anyNewPosts) {
                        item {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                            Text(
                                text = stringResource(R.string.load_more),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                                    .clickable {
                                        loaded = false
                                        homeScreenViewModel.getPostsByFilters(categoryValue, cityValue, maxPriceValue.toIntOrNull(), postsOrderType, reset = false)
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
                homeScreenViewModel.clearGetPostsState()
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
    if (showFiltersDialog) {
        val onDismiss = {
            showFiltersDialog = false
            categoryValue = homeScreenViewModel.lastCategory
            cityValue = homeScreenViewModel.lastCity
            maxPriceValue = homeScreenViewModel.lastMaxPrice?.toString() ?: ""
            postsOrderType = homeScreenViewModel.lastPostsOrderType
        }
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.filter_posts),
                        style = MaterialTheme.typography.titleLarge
                    )

                    FilterItemDropdown(
                        currentValue = categoryValue,
                        onValueChange = { categoryValue = it },
                        label = stringResource(R.string.category),
                        values = categories.toList(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    FilterItemDropdown(
                        currentValue = cityValue,
                        onValueChange = { cityValue = it },
                        label = stringResource(R.string.city),
                        values = cities.toList(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    FilterItemInputNumber(
                        currentValue = maxPriceValue,
                        onValueChange = { maxPriceValue = it.filter { it.isDigit() }.take(8) },
                        label = stringResource(R.string.max_price),
                        modifier = Modifier.fillMaxWidth(),
                        suffix = "₴"
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
                        Box(modifier = Modifier.weight(1f)) {
                            TextButton(
                                onClick = {
                                    categoryValue = ""
                                    cityValue = ""
                                    maxPriceValue = ""
                                }
                            ) {
                                Text(text = stringResource(R.string.clear_filters))
                            }
                        }
                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                showFiltersDialog = false
                                loaded = false
                                coroutineScope.launch {
                                    lazyListState.scrollToItem(0)
                                }
                                homeScreenViewModel.getPostsByFilters(categoryValue, cityValue, maxPriceValue.toIntOrNull(), postsOrderType, reset = true)
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
fun HomeScreenPreview() {
    HomeScreen {  }
}