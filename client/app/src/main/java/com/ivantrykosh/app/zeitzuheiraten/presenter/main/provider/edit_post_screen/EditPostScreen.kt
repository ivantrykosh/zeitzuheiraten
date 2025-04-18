package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.edit_post_screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.google.firebase.FirebaseNetworkException
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.domain.model.PostWithRating
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.DateRangePicker
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.ItemWithDropdownMenu
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.RatingView
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_CITIES_PER_POST
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_IMAGES_PER_POST
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_NOT_AVAILABLE_DATE_RANGES_PER_POST
import com.ivantrykosh.app.zeitzuheiraten.utils.Constants.MAX_SYMBOLS_FOR_POST_DESCRIPTION
import com.ivantrykosh.app.zeitzuheiraten.utils.isFileSizeAppropriate
import com.ivantrykosh.app.zeitzuheiraten.utils.toStringDate
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditPostScreen(
    editPostViewModel: EditPostViewModel = hiltViewModel(),
    postId: String,
    navigateBack: () -> Unit,
    navigateToPostFeedbacks: (String) -> Unit,
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    var citiesValue = remember { mutableStateListOf<String>() }
    val citiesValueError = stringResource(R.string.you_need_to_add_at_least_one_city)
    val cities = stringArrayResource(R.array.cities)
    var isCitiesExpanded by rememberSaveable { mutableStateOf(false) }

    var minPrice by rememberSaveable { mutableStateOf("") }
    val minPriceError = stringResource(R.string.min_price_is_required)

    var description by rememberSaveable { mutableStateOf("") }
    val descriptionError = stringResource(R.string.you_need_to_add_description)

    var notAvailableDateRanges = remember { mutableStateListOf<DatePair>() }
    var isDateRangePickerShowed by rememberSaveable { mutableStateOf(false) }

    var pickedImages = remember { mutableStateListOf<Uri>() }
    val pickedImagesError = stringResource(R.string.you_need_to_add_at_least_one_image)
    val pickPostImages = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_IMAGES_PER_POST)) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach {
                if (isFileSizeAppropriate(it, contentResolver)) {
                    pickedImages.add(it)
                } else {
                    Toast.makeText(context, R.string.file_is_larger_than_5_mb, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val getPostState by editPostViewModel.getPostByIdState.collectAsStateWithLifecycle()
    var post by remember { mutableStateOf<PostWithRating?>(null) }
    val updatePostState by editPostViewModel.updatePostState.collectAsStateWithLifecycle()
    val deletePostState by editPostViewModel.deletePostState.collectAsStateWithLifecycle()
    var loaded by remember { mutableStateOf(false) }
    var uploadNewImages by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var textInErrorDialog by remember { mutableStateOf("") }
    var showDeletePostDialog by remember { mutableStateOf(false) }

    LaunchedEffect(0) {
        loaded = false
        editPostViewModel.getPostById(postId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.edit_post)) },
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
                        onClick = { showDeletePostDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_post)
                        )
                    }
                },
                windowInsets = WindowInsets(top = 0.dp)
            )
        },
    ) {
        if (getPostState.data != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ItemWithDropdownMenu(
                    currentValue = post!!.category,
                    onValueChange = { },
                    label = R.string.category,
                    values = emptyList(),
                    enabled = false,
                )
                RatingView(post!!.rating, clickable = true, onClick = { navigateToPostFeedbacks(postId) })
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.city),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxRowHeight()
                            .wrapContentHeight(),
                    )
                    for (city in citiesValue) {
                        OutlinedButton(
                            onClick = { citiesValue.remove(city) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = city,
                                    fontSize = 16.sp,
                                )
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = stringResource(R.string.remove_city),
                                )
                            }
                        }
                    }
                    if (citiesValue.size < MAX_CITIES_PER_POST) {
                        OutlinedButton(
                            onClick = { isCitiesExpanded = true },
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.add_city),
                                    fontSize = 16.sp,
                                )
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = stringResource(R.string.add_city),
                                )
                            }
                            DropdownMenu(
                                expanded = isCitiesExpanded,
                                onDismissRequest = { isCitiesExpanded = false },
                            ) {
                                for (city in cities.filter { !citiesValue.contains(it) }) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = city,
                                                fontSize = 16.sp
                                            )
                                        },
                                        onClick = {
                                            isCitiesExpanded = false
                                            citiesValue.add(city)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.min_price),
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextField(
                        value = minPrice,
                        onValueChange = {
                            minPrice = it.filter { it.isDigit() }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        suffix = {
                            Text("₴")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                TextField(
                    value = description,
                    onValueChange = { description = it.take(MAX_SYMBOLS_FOR_POST_DESCRIPTION) },
                    minLines = 4,
                    maxLines = 4,
                    label = {
                        Text(text = stringResource(R.string.description))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text(
                            text = "${description.length}/$MAX_SYMBOLS_FOR_POST_DESCRIPTION",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.not_available_dates),
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                    for (date in notAvailableDateRanges) {
                        OutlinedButton(
                            onClick = { notAvailableDateRanges.remove(date) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${date.startDate.toStringDate()}-${date.endDate.toStringDate()}",
                                    fontSize = 16.sp,
                                )
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = stringResource(R.string.remove_dates),
                                )
                            }
                        }
                    }
                    if (notAvailableDateRanges.size < MAX_NOT_AVAILABLE_DATE_RANGES_PER_POST) {
                        OutlinedButton(
                            onClick = { isDateRangePickerShowed = true },
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.add_date_range),
                                    fontSize = 16.sp,
                                )
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = stringResource(R.string.add_date_range),
                                )
                            }
                        }
                    }
                }
                Column {
                    if (pickedImages.isEmpty()) {
                        OutlinedButton(
                            onClick = {
                                pickPostImages.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.add_images),
                                fontSize = 20.sp
                            )
                        }
                    }
                    for (index in 0..pickedImages.lastIndex step 2) {
                        Row {
                            AsyncImage(
                                model = pickedImages[index],
                                contentDescription = stringResource(id = R.string.picked_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(150.dp)
                                    .weight(1f)
                                    .padding(8.dp)
                            )
                            if (index + 1 <= pickedImages.lastIndex)
                                AsyncImage(
                                    model = pickedImages[index + 1],
                                    contentDescription = stringResource(id = R.string.picked_image),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .height(150.dp)
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                        }
                    }
                    if (pickedImages.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                pickedImages.clear()
                                uploadNewImages = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.remove_images),
                                fontSize = 20.sp
                            )
                        }
                    }
                }
                FilledTonalButton(
                    onClick = {
                        if (citiesValue.isEmpty()) {
                            showErrorDialog = true
                            textInErrorDialog = citiesValueError
                        } else if (minPrice.isEmpty()) {
                            showErrorDialog = true
                            textInErrorDialog = minPriceError
                        } else if (description.isEmpty()) {
                            showErrorDialog = true
                            textInErrorDialog = descriptionError
                        } else if (pickedImages.isEmpty()) {
                            showErrorDialog = true
                            textInErrorDialog = pickedImagesError
                        } else {
                            loaded = false
                            editPostViewModel.updatePost(
                                postId,
                                citiesValue,
                                minPrice.toInt(),
                                description,
                                notAvailableDateRanges,
                                pickedImages,
                                uploadNewImages,
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.edit_post),
                        fontSize = 20.sp
                    )
                }
            }
        }
        if (isDateRangePickerShowed) {
            DateRangePicker(
                onDateRangeSelected = {
                    notAvailableDateRanges.add(it)
                },
                onDismiss = { isDateRangePickerShowed = false }
            )
        }
    }

    if (!loaded) {
        when {
            getPostState.loading || updatePostState.loading || deletePostState.loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
            }

            deletePostState.error != null -> {
                loaded = true
                when (deletePostState.error) {
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
            updatePostState.error != null -> {
                loaded = true
                when (updatePostState.error) {
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

            deletePostState.data != null -> {
                loaded = true
                Toast.makeText(context, R.string.post_was_deleted, Toast.LENGTH_LONG).show()
                navigateBack()
            }
            updatePostState.data != null -> {
                loaded = true
                Toast.makeText(context, R.string.post_was_updated, Toast.LENGTH_LONG).show()
                navigateBack()
            }
            getPostState.data != null -> {
                loaded = true
                post = getPostState.data!!
                citiesValue.addAll(getPostState.data!!.cities)
                minPrice = getPostState.data!!.minPrice.toString()
                description = getPostState.data!!.description
                notAvailableDateRanges.addAll(getPostState.data!!.notAvailableDates)
                pickedImages.addAll(getPostState.data!!.photosUrl.map { it.toUri() })
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
    if (showDeletePostDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Text(
                    text = stringResource(id = R.string.ok_title),
                    modifier = Modifier.clickable {
                        showDeletePostDialog = false
                        loaded = false
                        editPostViewModel.deletePostState(postId)
                    }
                )
            },
            dismissButton = {
                Text(
                    text = stringResource(id = R.string.cancel),
                    modifier = Modifier.clickable {
                        showDeletePostDialog = false
                    }
                )
            },
            title = { Text(text = stringResource(id = R.string.delete_post)) },
            text = { Text(text = stringResource(id = R.string.you_want_to_delete_post)) }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun EditPostScreenPreview() {
    EditPostScreen(
        navigateBack = {},
        postId = "33fkjto43j",
        navigateToPostFeedbacks = {}
    )
}