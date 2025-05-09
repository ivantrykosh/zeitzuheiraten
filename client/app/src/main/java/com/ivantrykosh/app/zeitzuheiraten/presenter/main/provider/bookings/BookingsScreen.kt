package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.bookings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.CustomCircularProgressIndicator
import com.ivantrykosh.app.zeitzuheiraten.utils.BookingsFilterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    bookingsViewModel: BookingsViewModel = hiltViewModel(),
    navigateToEditPost: (String) -> Unit,
    navigateToUser: (String) -> Unit,
) {
    val postsState by bookingsViewModel.getPostsState.collectAsStateWithLifecycle()
    val bookingsState by bookingsViewModel.getBookings.collectAsStateWithLifecycle()
    val cancelBookingState by bookingsViewModel.cancelBookingState.collectAsStateWithLifecycle()
    val confirmBookingState by bookingsViewModel.confirmBookingState.collectAsStateWithLifecycle()
    val bookings by bookingsViewModel.lastBookings.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var postsLoaded by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var textInErrorDialog by rememberSaveable { mutableStateOf("") }

    var isCancelDialogShowed by rememberSaveable { mutableStateOf(false) }
    var isConfirmBookingDialogShowed by rememberSaveable { mutableStateOf(false) }

    var pickedBookingFilterType by rememberSaveable { mutableStateOf(BookingsFilterType.NOT_CONFIRMED) }
    var pickedBookingId by rememberSaveable { mutableStateOf<String?>(null) }
    var pickedPostId by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = postsState.loading || bookingsState.loading || cancelBookingState.loading || confirmBookingState.loading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            loaded = false
            bookingsViewModel.getPosts()
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
                title = { Text(text = stringResource(R.string.bookings)) },
                windowInsets = WindowInsets(top = 0.dp),
            )
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (postsState.data != null) {
                    items(postsState.data!!) { post ->
                        val isCurrent = pickedPostId == post.id
                        TextButton(
                            onClick = {
                                pickedPostId = post.id
                                loaded = false
                                bookingsViewModel.clearLastBookings()
                                bookingsViewModel.getBookingsForPost(pickedPostId!!, pickedBookingFilterType, reset = true)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (isCurrent) Color.LightGray else Color.White,
                                contentColor = if (isCurrent) Color.Black else Color.DarkGray
                            )
                        ) {
                            Text(
                                text = post.category,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            LazyRow(
                contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(BookingsFilterType.entries) { filter ->
                    val isCurrent = pickedBookingFilterType == filter
                    TextButton(
                        onClick = {
                            pickedBookingFilterType = filter
                            loaded = false
                            bookingsViewModel.clearLastBookings()
                            bookingsViewModel.getBookingsForPost(pickedPostId!!, pickedBookingFilterType, reset = true)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (isCurrent) Color.LightGray else Color.White,
                            contentColor = if (isCurrent) Color.Black else Color.DarkGray
                        )
                    ) {
                        Text(
                            text = filter.getString(context),
                            fontSize = 16.sp
                        )
                    }
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (bookings.isNotEmpty()) {
                    items(bookings) { booking ->
                        BookingView(
                            booking = booking,
                            onCategoryClicked = { navigateToEditPost(it) },
                            onUserClicked = { navigateToUser(it) },
                            onCancelBooking = {
                                loaded = false
                                isCancelDialogShowed = true
                                pickedBookingId = it
                            },
                            onConfirmBooking = {
                                loaded = false
                                isConfirmBookingDialogShowed = true
                                pickedBookingId = it
                            },
                        )
                    }
                    if (bookingsViewModel.anyNewBookings) {
                        item {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                            Text(
                                text = stringResource(R.string.load_more),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        loaded = false
                                        bookingsViewModel.getBookingsForPost(pickedPostId!!, pickedBookingFilterType, reset = false)
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                } else if (!bookingsState.loading && postsLoaded) {
                    item {
                        Text(
                            text = stringResource(R.string.no_bookings_found),
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
            postsState.loading || bookingsState.loading || cancelBookingState.loading || confirmBookingState.loading -> { }
            postsState.error != null -> {
                loaded = true
                when (postsState.error) {
                    is FirebaseNetworkException -> {
                        textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        showErrorDialog = true
                    }

                    else -> {
                        textInErrorDialog = stringResource(id = R.string.error_occurred)
                        showErrorDialog = true
                    }
                }
                bookingsViewModel.clearGetPostsState()
            }
            bookingsState.error != null -> {
                loaded = true
                when (bookingsState.error) {
                    is FirebaseNetworkException -> {
                        textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        showErrorDialog = true
                    }

                    else -> {
                        textInErrorDialog = stringResource(id = R.string.error_occurred)
                        showErrorDialog = true
                    }
                }
                bookingsViewModel.clearGetBookingsState()
            }
            cancelBookingState.error != null -> {
                loaded = true
                when (cancelBookingState.error) {
                    is FirebaseNetworkException -> {
                        textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        showErrorDialog = true
                    }

                    else -> {
                        textInErrorDialog = stringResource(id = R.string.error_occurred)
                        showErrorDialog = true
                    }
                }
                bookingsViewModel.clearCancelBookingState()
            }
            confirmBookingState.error != null -> {
                loaded = true
                when (confirmBookingState.error) {
                    is FirebaseNetworkException -> {
                        textInErrorDialog = stringResource(id = R.string.no_internet_connection)
                        showErrorDialog = true
                    }

                    else -> {
                        textInErrorDialog = stringResource(id = R.string.error_occurred)
                        showErrorDialog = true
                    }
                }
                bookingsViewModel.clearConfirmBookingState()
            }
            else -> {
                if (cancelBookingState.data != null) {
                    Toast.makeText(LocalContext.current, R.string.the_booking_was_canceled, Toast.LENGTH_LONG).show()
                    bookingsViewModel.clearCancelBookingState()
                    bookingsViewModel.getBookingsForPost(pickedPostId!!, pickedBookingFilterType, reset = true)
                }
                if (confirmBookingState.data != null) {
                    Toast.makeText(LocalContext.current, R.string.the_booking_was_confirmed, Toast.LENGTH_LONG).show()
                    bookingsViewModel.clearConfirmBookingState()
                    bookingsViewModel.getBookingsForPost(pickedPostId!!, pickedBookingFilterType, reset = true)
                }
                if (bookingsState.data != null) {
                    loaded = true
                }
                if (postsState.data != null && !postsLoaded) {
                    loaded = true
                    pickedPostId = postsState.data!!.getOrNull(0)?.id
                    if (pickedPostId != null) {
                        bookingsViewModel.getBookingsForPost(pickedPostId!!, pickedBookingFilterType, reset = true)
                    }
                    postsLoaded = true
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
    if (isCancelDialogShowed) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable {
                loaded = false
                isCancelDialogShowed = false
                bookingsViewModel.cancelBooking(pickedBookingId!!)
            }) },
            dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable {
                isCancelDialogShowed = false
            }) },
            title = { Text(text = stringResource(id = R.string.cancel_booking)) },
            text = { Text(text = stringResource(id = R.string.cancel_booking_question)) }
        )
    }
    if (isConfirmBookingDialogShowed) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable {
                loaded = false
                isConfirmBookingDialogShowed = false
                bookingsViewModel.confirmBooking(pickedBookingId!!)
            }) },
            dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable {
                isConfirmBookingDialogShowed = false
            }) },
            title = { Text(text = stringResource(id = R.string.confirm_booking)) },
            text = { Text(text = stringResource(id = R.string.confirm_booking_question)) }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun BookingsScreenPreview() {
    BookingsScreen(
        navigateToEditPost = {},
        navigateToUser = {},
    )
}