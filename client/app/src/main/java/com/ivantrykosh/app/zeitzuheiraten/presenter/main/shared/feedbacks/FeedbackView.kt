package com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.feedbacks

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.utils.toStringDate

@Composable
fun FeedbackView(
    feedback: Feedback
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.rating) + ": " + feedback.rating,
                fontSize = 20.sp,
            )
            Text(
                text = feedback.username,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.clickable {
                    // todo maybe add navigation to user page
                }
            )
            Text(
                text = feedback.description,
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp,
            )
            Text(
                text = feedback.date.toStringDate(),
                fontSize = 16.sp
            )
        }
    }
}