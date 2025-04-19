package com.ivantrykosh.app.zeitzuheiraten.presenter.main

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

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