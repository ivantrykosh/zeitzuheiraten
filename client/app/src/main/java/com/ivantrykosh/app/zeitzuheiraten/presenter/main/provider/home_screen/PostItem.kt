package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.home_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Post

@Composable
fun PostItem(
    post: Post,
    onPostClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onPostClick(post.id)
            }
    ) {
        AsyncImage(
            model = post.photosUrl[0],
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.category,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.from_price, post.minPrice),
                    fontSize = 18.sp,
                )
            }
            Text(
                text = post.cities.joinToString(", "),
                fontSize = 18.sp,
            )
            Text(
                text = post.description,
                fontSize = 16.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

@Composable
@Preview
fun PostItemPreview() {
    PostItem(
        post = Post(
            id = "ddd",
            providerId = "ddksl",
            providerName = "somename",
            category = "Photo",
            cities = listOf("Lviv", "Kyiv"),
            description = "someDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescriptionsomeDescription",
            minPrice = 1000,
            photosUrl = listOf("imageUrl"),
            notAvailableDates = listOf()
        ),
        onPostClick = {}
    )
}