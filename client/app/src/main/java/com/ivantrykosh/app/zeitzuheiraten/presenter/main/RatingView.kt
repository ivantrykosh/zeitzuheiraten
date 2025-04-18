package com.ivantrykosh.app.zeitzuheiraten.presenter.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Rating

@Composable
fun RatingView(
    rating: Rating,
    clickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .let {
                if (clickable) {
                    it.clickable(onClick = onClick)
                } else {
                    it
                }
            }
    ) {
        repeat(5) { index ->
            val filled = when {
                rating.rating >= index + 1 -> 1f
                rating.rating > index -> rating.rating.toFloat() - index
                else -> 0f
            }

            Box(modifier = Modifier.size(24.dp)) {
                // Empty star
                Image(
                    painter = painterResource(R.drawable.baseline_star_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Gray),
                    modifier = Modifier.fillMaxSize().scale(1.2f)
                )
                if (filled > 0f) {
                    ClipStarOverlay(
                        fraction = filled,
                        color = Color.Yellow
                    )
                }
            }
        }
        Text(
            text = stringResource(R.string.feedbacks, rating.numberOfFeedbacks),
            modifier = Modifier.padding(start = 10.dp),
            fontSize = 16.sp
        )
    }
}

@Composable
@Preview(showBackground = true)
fun RatingViewPreview() {
    RatingView(Rating(3.9, 20))
}

@Composable
fun ClipStarOverlay(
    fraction: Float,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                clip = true
                shape = RectangleShape
            }
            .drawWithContent {
                val width = size.width * fraction
                clipRect(right = width) {
                    this@drawWithContent.drawContent()
                }
            }
    ) {
        Image(
            painter = painterResource(R.drawable.baseline_star_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.matchParentSize().scale(1.2f)
        )
    }
}