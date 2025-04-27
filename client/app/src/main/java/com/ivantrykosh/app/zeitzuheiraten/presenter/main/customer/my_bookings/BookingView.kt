package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_bookings

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.Booking
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.presenter.ui.theme.Orange
import com.ivantrykosh.app.zeitzuheiraten.utils.toStringDate

@Composable
fun BookingView(
    booking: Booking,
    onCategoryClicked: (String) -> Unit,
    onProviderClicked: (String) -> Unit,
    onChangeDate: (Booking) -> Unit,
    onCancelBooking: (String) -> Unit,
    onLeaveFeedback: (String) -> Unit,
    onConfirmServiceProviding: (String) -> Unit,
) {
    var isContextMenuVisible by rememberSaveable { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val categoriesWithStandardBooking = stringArrayResource(R.array.categories_with_standard_booking)

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
                text = booking.category,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.clickable {
                    onCategoryClicked(booking.postId)
                }
            )
            Text(
                text = booking.provider,
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    onProviderClicked(booking.providerId)
                }
            )
            val dateText = if (categoriesWithStandardBooking.contains(booking.category)) {
                "${booking.dateRange.startDate.toStringDate()}-${booking.dateRange.endDate.toStringDate()}"
            } else {
                stringResource(R.string.deadline_to, booking.dateRange.startDate.toStringDate())
            }
            Text(
                text = dateText,
                fontSize = 16.sp
            )
            val color = when {
                booking.serviceProvided -> Color.Gray
                booking.canceled -> Color.Red
                booking.confirmed -> Color.Green
                else -> Orange
            }
            val status = when {
                booking.serviceProvided -> R.string.service_provided
                booking.canceled -> R.string.booking_canceled
                booking.confirmed -> R.string.booking_confirmed
                else -> R.string.booking_not_confirmed
            }
            Text(text = stringResource(status).uppercase(), color = color)
        }
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            if (!booking.canceled && !booking.serviceProvided) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.customer_menu_change_date))
                    },
                    onClick = {
                        onChangeDate(booking)
                        isContextMenuVisible = false
                    }
                )
            }
            if (!booking.canceled && !booking.serviceProvided) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.customer_menu_cancel))
                    },
                    onClick = {
                        onCancelBooking(booking.id)
                        isContextMenuVisible = false
                    }
                )
            }
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.customer_menu_leave_feedback))
                },
                onClick = {
                    onLeaveFeedback(booking.postId)
                    isContextMenuVisible = false
                }
            )
            if (!booking.canceled && !booking.serviceProvided && booking.confirmed) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.customer_menu_confirm_service_providing))
                    },
                    onClick = {
                        onConfirmServiceProviding(booking.id)
                        isContextMenuVisible = false
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BookingViewPreview() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        BookingView(
            booking = Booking(
                id = "id123456",
                userId = "userId123456",
                username = "Customer Name",
                postId = "postId123456",
                category = "Photography",
                provider = "Provider Name",
                dateRange = DatePair(startDate = 1744761600000, endDate = 1745366400000),
                confirmed = false,
                canceled = false,
                serviceProvided = false,
            ),
            onCategoryClicked = {},
            onChangeDate = {},
            onCancelBooking = {},
            onLeaveFeedback = {},
            onConfirmServiceProviding = {},
            onProviderClicked = {}
        )
    }
}