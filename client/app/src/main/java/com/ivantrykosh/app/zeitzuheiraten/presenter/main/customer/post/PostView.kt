package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
fun PostView(
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
            Text(
                text = post.providerName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.category,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.from_price, post.minPrice),
                    fontSize = 18.sp,
                )
            }
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
fun PostViewPreview() {
    PostView(
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
        onPostClick = {}
    )
}