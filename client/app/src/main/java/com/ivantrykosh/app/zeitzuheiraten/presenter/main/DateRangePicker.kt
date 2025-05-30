package com.ivantrykosh.app.zeitzuheiraten.presenter.main

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.utils.toStringDate
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
    selectableDates: BookingSelectableDates
) {
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = selectableDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null,
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
                    text = stringResource(R.string.select_date_range),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                )
            },
            headline = {
                val startDate = dateRangePickerState.selectedStartDateMillis?.toStringDate() ?: ""
                val endDate = dateRangePickerState.selectedEndDateMillis?.toStringDate() ?: ""
                val dates = if (startDate.isEmpty()) {
                    ""
                } else if (endDate.isEmpty()) {
                    startDate
                } else {
                    "$startDate - $endDate"
                }
                Text(
                    text = dates,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            },
            showModeToggle = false,
            modifier = Modifier.height(500.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DateRangePickerPreview() {
    DateRangePicker(
        onDateRangeSelected = { },
        onDismiss = { },
        selectableDates = BookingSelectableDates(listOf(DatePair(1746392400000, 1746392400000)))
    )
}