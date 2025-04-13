package com.ivantrykosh.app.zeitzuheiraten.presenter.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
class BookingSelectableDates(private val unavailableDates: List<DatePair>): SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val todayOrLater = System.currentTimeMillis() / 86_400_000 <= utcTimeMillis / 86_400_000
        val available = unavailableDates.none { it.startDate <= utcTimeMillis && utcTimeMillis <= it.endDate }
        return todayOrLater && available
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDateTime.now().year
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(
    onDateRangeSelected: (DatePair) -> Unit,
    onDismiss: () -> Unit,
    selectableDates: BookingSelectableDates = BookingSelectableDates(emptyList())
) {
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = selectableDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        DatePair(
                            dateRangePickerState.selectedStartDateMillis!!,
                            dateRangePickerState.selectedEndDateMillis!!
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.ok_title))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}