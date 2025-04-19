package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.chats.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivantrykosh.app.zeitzuheiraten.domain.model.ChatMessage
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.TextWithLinks
import com.ivantrykosh.app.zeitzuheiraten.utils.toStringDateTime

@Composable
fun MessageView(
    message: ChatMessage,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMyMessage) Arrangement.End else Arrangement.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = if (message.isMyMessage) Arrangement.End else Arrangement.Start
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isMyMessage) Color.Green else Color.LightGray,
                    contentColor = Color.Black,
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalAlignment = if (message.isMyMessage) Alignment.End else Alignment.Start
                ) {
                    TextWithLinks(message.message)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.dateTime.toStringDateTime()
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MessageViewPreview() {
    Scaffold {
        Column(
            modifier = Modifier.padding(it).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MessageView(
                ChatMessage(
                    id = "TODO()",
                    message = "Hello, I'm interested in your services, https://www.youtube.com/",
                    isMyMessage = false,
                    dateTime = System.currentTimeMillis()
                )
            )
            MessageView(
                ChatMessage(
                    id = "TODO()",
                    message = "Hello, I'm interested in your services, https://www.youtube.com/",
                    isMyMessage = true,
                    dateTime = System.currentTimeMillis()
                )
            )
            MessageView(
                ChatMessage(
                    id = "TODO()",
                    message = "Hello, I'm interested in your services, https://www.youtube.com/",
                    isMyMessage = false,
                    dateTime = System.currentTimeMillis()
                )
            )
            MessageView(
                ChatMessage(
                    id = "TODO()",
                    message = "Hello, I'm interested in your services, https://www.youtube.com/",
                    isMyMessage = true,
                    dateTime = System.currentTimeMillis()
                )
            )
            MessageView(
                ChatMessage(
                    id = "TODO()",
                    message = "Bye",
                    isMyMessage = false,
                    dateTime = System.currentTimeMillis()
                )
            )
            MessageView(
                ChatMessage(
                    id = "TODO()",
                    message = "Bye",
                    isMyMessage = true,
                    dateTime = System.currentTimeMillis()
                )
            )
        }
    }
}