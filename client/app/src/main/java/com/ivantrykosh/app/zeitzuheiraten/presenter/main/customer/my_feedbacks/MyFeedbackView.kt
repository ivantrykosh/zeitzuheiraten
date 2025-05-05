package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_feedbacks

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Feedback
import com.ivantrykosh.app.zeitzuheiraten.utils.toStringDate

@Composable
fun MyFeedbackView(
    feedback: Feedback,
    onDeleteClicked: (String) -> Unit,
    navigateToPost: () -> Unit,
) {
    var isContextMenuVisible by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .indication(interactionSource, LocalIndication.current)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        },
                        onPress = {
                            val press = PressInteraction.Press(it)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                        }
                    )
                }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.rating) + ": " + feedback.rating,
                fontSize = 20.sp,
            )
            Column(
                modifier = Modifier.clickable {
                    navigateToPost()
                }
            ) {
                Text(
                    text = feedback.category,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = stringResource(R.string.for_provider, feedback.provider),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
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
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.delete_feedback))
                },
                onClick = {
                    onDeleteClicked(feedback.id)
                    isContextMenuVisible = false
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MyFeedbackViewPreview() {
    MyFeedbackView(
        feedback = Feedback(
            id = "TODO()",
            userId = "TODO()",
            username = "TODO()",
            postId = "TODO()",
            category = "Photography",
            provider = "Some provider",
            rating = 4,
            description = "Some description".repeat(5),
            date = System.currentTimeMillis()
        ),
        onDeleteClicked = {},
        navigateToPost = {}
    )
}