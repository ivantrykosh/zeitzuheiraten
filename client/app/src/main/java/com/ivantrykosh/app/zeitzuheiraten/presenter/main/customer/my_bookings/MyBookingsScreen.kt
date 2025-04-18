package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_bookings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.BookingSelectableDates
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.DateRangePicker
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_SYMBOLS_FOR_FEEDBACK_DESCRIPTION

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    myBookingsViewModel: MyBookingsViewModel = hiltViewModel(),
    navigateToPost: (String) -> Unit,
) {
    val bookingsState by myBookingsViewModel.getBookings.collectAsStateWithLifecycle()
    val getNotAvailableDatesState by myBookingsViewModel.getNotAvailableDatesState.collectAsStateWithLifecycle()
    val changeDateState by myBookingsViewModel.changeDateState.collectAsStateWithLifecycle()
    val cancelBookingState by myBookingsViewModel.cancelBookingState.collectAsStateWithLifecycle()
    val confirmProvidingState by myBookingsViewModel.confirmProvidingState.collectAsStateWithLifecycle()
    val createFeedbackState by myBookingsViewModel.createFeedbackState.collectAsStateWithLifecycle()
    val bookings by myBookingsViewModel.lastBookings.collectAsStateWithLifecycle()
    var loaded by rememberSaveable { mutableStateOf(false) }
    var dateLoaded by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var textInErrorDialog by remember { mutableStateOf("") }

    var isDateRangePickerShowed by rememberSaveable { mutableStateOf(false) }
    var isCancelDialogShowed by rememberSaveable { mutableStateOf(false) }
    var isConfirmProvidingDialogShowed by rememberSaveable { mutableStateOf(false) }
    var isFeedbackShowed by rememberSaveable { mutableStateOf(false) }

    var pickedBookingId by rememberSaveable { mutableStateOf<String?>(null) }
    var pickedPostId by rememberSaveable { mutableStateOf<String?>(null) }
    var category by rememberSaveable { mutableStateOf("") }
    var provider by rememberSaveable { mutableStateOf("") }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = bookingsState.loading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            loaded = false
            myBookingsViewModel.getBookings()
        },
        indicator = { state, _ ->
            if (state.isRefreshing) {
                CircularProgressIndicator(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize())
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(text = stringResource(R.string.my_bookings)) },
                windowInsets = WindowInsets(top = 0.dp),
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (bookings.isNotEmpty()) {
                    items(bookings) { booking ->
                        BookingView(
                            booking = booking,
                            onCategoryClicked = { navigateToPost(it) },
                            onChangeDate = {
                                loaded = false
                                dateLoaded = false
                                pickedBookingId = it.id
                                myBookingsViewModel.getNotAvailableDates(it.postId, it.dateRange)
                            },
                            onCancelBooking = {
                                loaded = false
                                isCancelDialogShowed = true
                                pickedBookingId = it
                            },
                            onLeaveFeedback = {
                                category = booking.category
                                provider = booking.provider
                                isFeedbackShowed = true
                                pickedPostId = it
                            },
                            onConfirmServiceProviding = {
                                loaded = false
                                isConfirmProvidingDialogShowed = true
                                pickedBookingId = it
                            },
                        )
                    }
                    if (myBookingsViewModel.anyNewBookings) {
                        item {
                            Divider(modifier = Modifier.fillMaxWidth())
                            Text(
                                text = stringResource(R.string.load_more),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        loaded = false
                                        myBookingsViewModel.getNewBookings()
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                } else {
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
            bookingsState.loading || getNotAvailableDatesState.loading || changeDateState.loading || cancelBookingState.loading || confirmProvidingState.loading || createFeedbackState.loading -> {
                CircularProgressIndicator(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize())
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
            }
            getNotAvailableDatesState.error != null -> {
                loaded = true
                when (getNotAvailableDatesState.error) {
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
            changeDateState.error != null -> {
                loaded = true
                when (changeDateState.error) {
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
            }
            confirmProvidingState.error != null -> {
                loaded = true
                when (confirmProvidingState.error) {
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
            createFeedbackState.error != null -> {
                loaded = true
                when (createFeedbackState.error) {
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
            else -> {
                if (changeDateState.data != null) {
                    Toast.makeText(LocalContext.current, R.string.the_date_was_changed, Toast.LENGTH_LONG).show()
                    myBookingsViewModel.clearChangeDateState()
                    myBookingsViewModel.getBookings()
                }
                if (cancelBookingState.data != null) {
                    Toast.makeText(LocalContext.current, R.string.the_booking_was_canceled, Toast.LENGTH_LONG).show()
                    myBookingsViewModel.clearCancelBookingState()
                    myBookingsViewModel.getBookings()
                }
                if (confirmProvidingState.data != null) {
                    Toast.makeText(LocalContext.current, R.string.the_service_was_provided, Toast.LENGTH_LONG).show()
                    myBookingsViewModel.clearConfirmProvidingState()
                    myBookingsViewModel.getBookings()
                }
                if (createFeedbackState.data != null) {
                    Toast.makeText(LocalContext.current, R.string.the_feedback_was_leaved, Toast.LENGTH_LONG).show()
                    myBookingsViewModel.clearCreateFeedbackState()
                    myBookingsViewModel.getBookings()
                }
                if (bookingsState.data != null) {
                    loaded = true
                }
                if (getNotAvailableDatesState.data != null && !dateLoaded) {
                    loaded = true
                    dateLoaded = true
                    isDateRangePickerShowed = true
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
    if (isDateRangePickerShowed) {
        DateRangePicker(
            onDateRangeSelected = {
                loaded = false
                myBookingsViewModel.changeDate(pickedBookingId!!, it)
            },
            onDismiss = { isDateRangePickerShowed = false },
            selectableDates = BookingSelectableDates(getNotAvailableDatesState.data!!)
        )
    }
    if (isCancelDialogShowed) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable {
                loaded = false
                isCancelDialogShowed = false
                myBookingsViewModel.cancelBooking(pickedBookingId!!)
            }) },
            dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable {
                isCancelDialogShowed = false
            }) },
            title = { Text(text = stringResource(id = R.string.cancel_booking)) },
            text = { Text(text = stringResource(id = R.string.cancel_booking_question)) }
        )
    }
    if (isConfirmProvidingDialogShowed) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable {
                loaded = false
                isConfirmProvidingDialogShowed = false
                myBookingsViewModel.confirmServiceProviding(pickedBookingId!!)
            }) },
            dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable {
                isConfirmProvidingDialogShowed = false
            }) },
            title = { Text(text = stringResource(id = R.string.confirm_service_providing)) },
            text = { Text(text = stringResource(id = R.string.confirm_service_providing_question)) }
        )
    }
    if (isFeedbackShowed) {
        FeedbackDialog(
            onOkPressed = { rating, description ->
                loaded = false
                isFeedbackShowed = false
                myBookingsViewModel.createFeedback(pickedPostId!!, category, provider, rating, description)
            },
            onDismiss = {
                isFeedbackShowed = false
            }
        )
    }
}

@Composable
fun FeedbackDialog(
    onOkPressed: (Int, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var rating by rememberSaveable { mutableStateOf<Int?>(null) }
    var isRatingError by rememberSaveable { mutableStateOf(false) }
    val ratingErrorMessage = stringResource(R.string.you_need_to_choose_rating)
    var description by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable {
            if (rating == null) {
                isRatingError = true
            } else {
                onOkPressed(rating!!, description.trim())
            }
        }) },
        dismissButton = { Text(text = stringResource(id = R.string.cancel), modifier = Modifier.clickable {
            onDismiss()
        }) },
        title = { Text(text = stringResource(id = R.string.leave_feedback)) },
        text = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(5) { index ->
                        Icon(
                            painter = painterResource(R.drawable.baseline_star_24),
                            contentDescription = stringResource(R.string.rating),
                            tint = if (index < (rating ?: 0)) Color.Yellow else Color.Gray,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    rating = index + 1
                                }
                        )
                    }
                }
                Text(
                    text = if (isRatingError) ratingErrorMessage else "",
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                TextField(
                    value = description,
                    onValueChange = { description = it.take(MAX_SYMBOLS_FOR_FEEDBACK_DESCRIPTION) },
                    minLines = 3,
                    maxLines = 3,
                    label = {
                        Text(text = stringResource(R.string.description_optional))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text(
                            text = "${description.length}/$MAX_SYMBOLS_FOR_FEEDBACK_DESCRIPTION",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun MyBookingsScreenPreview() {
    MyBookingsScreen(
        navigateToPost = {}
    )
}