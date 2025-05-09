package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.chats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DisplayedChat

@Composable
fun ChatView(
    chat: DisplayedChat,
    onChatClicked: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onChatClicked(chat.id)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = chat.withUsername ?: stringResource(R.string.deleted_user),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChatViewPreview() {
    ChatView(
        chat = DisplayedChat(
            id = "TODO()",
            withUserId = "userId",
            withUsername = "User Name",
        ),
        onChatClicked = {},
    )
}