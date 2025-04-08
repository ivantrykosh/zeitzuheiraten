package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPostView(
    post: Post,
    navigateBack: () -> Unit,
    onProviderClicked: (String) -> Unit,
) {
    var clickedImage by rememberSaveable { mutableStateOf<String?>(null) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (post.photosUrl.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(post.photosUrl) { url ->
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
                text = post.providerName,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onProviderClicked(post.providerId) }
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.category,
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
                    text = post.cities.joinToString(),
                    fontSize = 18.sp,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.price_from, post.minPrice),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextWithLinks(text = post.description)

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = {
                        /* todo open chat with provider */
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
                        /* todo book the service */
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
}

@Composable
fun TextWithLinks(text: String) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val uriHandler = LocalUriHandler.current

    val annotatedText = buildAnnotatedString {
        val regex = "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)".toRegex()
        var lastIndex = 0

        for (match in regex.findAll(text)) {
            val start = match.range.first
            val end = match.range.last + 1

            append(text.substring(lastIndex, start))

            pushStringAnnotation(tag = "URL", annotation = match.value)
            withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                append(match.value)
            }
            pop()

            lastIndex = end
        }

        append(text.substring(lastIndex))
    }

    Text(
        text = annotatedText,
        fontSize = 16.sp,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures { offsetPosition ->
                layoutResult.value?.let { layout ->
                    val offset = layout.getOffsetForPosition(offsetPosition)
                    annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            uriHandler.openUri(annotation.item)
                        }
                }
            }
        },
        onTextLayout = { layoutResult.value = it }
    )
}

@Composable
@Preview(showBackground = true)
fun FullPostViewPreview() {
    FullPostView(
        post = Post(
            id = "ssdpu482489gh9",
            providerId = "dddkljie",
            providerName = "Поздняк Павло",
            category = "Фотограф",
            cities = listOf("Вінниця", "Хмельницький"),
            description = """Весільний, сімейний та репортажний фотограф у Вінниці та інших містах 
                |Мій профіль: https://vesilla.com.ua/profi/fotograf/4689-pozdnyak-pavlo.html
                |Ютуб: https://youtube.com""".trimMargin(),
            minPrice = 4000,
            photosUrl = listOf("https://vesilla.com.ua/uploads/posts/2022-08/1659343626_1926x2894.jpg", "https://vesilla.com.ua/uploads/posts/2022-08/1659343699_4158x2767.jpg", "https://vesilla.com.ua/uploads/posts/2022-08/1659343656_2699x4056.jpg"),
            notAvailableDates = emptyList(),
        ),
        navigateBack = {},
        onProviderClicked = {}
    )
}