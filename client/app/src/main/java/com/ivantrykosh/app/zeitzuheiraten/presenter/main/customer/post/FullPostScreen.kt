package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.post

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.BookingSelectableDates
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.DateRangePicker
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.RatingView
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.TextWithLinks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPostScreen(
    fullPostScreenViewModel: FullPostScreenModel = hiltViewModel(),
    postId: String,
    navigateBack: () -> Unit,
    onProviderClicked: (String) -> Unit,
    onOpenChatClicked: (String, String) -> Unit,
    navigateToPostFeedbacks: (String) -> Unit,
) {
    val getPostState by fullPostScreenViewModel.getPostByIdState.collectAsStateWithLifecycle()
    var post by remember { mutableStateOf<PostWithRating?>(null) }
    val getNotAvailableDatesState by fullPostScreenViewModel.getNotAvailableDatesState.collectAsStateWithLifecycle()
    val createBookingState by fullPostScreenViewModel.createBookingState.collectAsStateWithLifecycle()
    var loaded by remember { mutableStateOf(false) }
    var dateLoaded by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var textInErrorDialog by remember { mutableStateOf("") }

    var isDateRangePickerShowed by rememberSaveable { mutableStateOf(false) }

    var clickedImage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(0) {
        loaded = false
        fullPostScreenViewModel.getPostById(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.post_view)) },
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
                windowInsets = WindowInsets(top = 0.dp)
            )
        },
    ) {
        if (post != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                if (post!!.photosUrl.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(post!!.photosUrl) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = stringResource(R.string.post_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillParentMaxHeight()
                                    .aspectRatio(3f / 2f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { clickedImage = url }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = post!!.providerName,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onProviderClicked(post!!.providerId) }
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = post!!.category,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.cities)
                    )
                    Text(
                        text = post!!.cities.joinToString(),
                        fontSize = 18.sp,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.price_from, post!!.minPrice),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                RatingView(
                    post!!.rating,
                    clickable = true,
                    onClick = { navigateToPostFeedbacks(postId) })
                Spacer(modifier = Modifier.height(12.dp))

                TextWithLinks(text = post!!.description)

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(
                        onClick = {
                            onOpenChatClicked(post!!.providerId, post!!.providerName)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(2.dp, color = Color.Black)
                    ) {
                        Text(text = stringResource(R.string.open_chat).uppercase())
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            loaded = false
                            dateLoaded = false
                            fullPostScreenViewModel.getNotAvailableDates(postId)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(2.dp, color = Color.Black)
                    ) {
                        Text(text = stringResource(R.string.book).uppercase())
                    }
                }
            }
        }
    }

    if (clickedImage != null) {
        Dialog(
            onDismissRequest = { clickedImage = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AsyncImage(
                model = clickedImage,
                contentDescription = stringResource(id = R.string.post_image),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { clickedImage = null }
            )
        }
    }

    if (!loaded) {
        when {
            getPostState.loading || getNotAvailableDatesState.loading || createBookingState.loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
            }
            getPostState.error != null -> {
                loaded = true
                when (getPostState.error) {
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
            createBookingState.error != null -> {
                loaded = true
                when (createBookingState.error) {
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
                if (createBookingState.data != null) {
                    loaded = true
                    Toast.makeText(LocalContext.current, R.string.the_service_was_booked, Toast.LENGTH_LONG).show()
                    navigateBack()
                }
                if (getPostState.data != null) {
                    loaded = true
                    post = getPostState.data
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
                fullPostScreenViewModel.bookService(postId, it)
            },
            onDismiss = { isDateRangePickerShowed = false },
            selectableDates = BookingSelectableDates(getNotAvailableDatesState.data!!)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun FullPostScreenPreview() {
    FullPostScreen(
        postId = "someId",
        navigateBack = {},
        onProviderClicked = {},
        onOpenChatClicked = { _, _ -> },
        navigateToPostFeedbacks = {}
    )
}