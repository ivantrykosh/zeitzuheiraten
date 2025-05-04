package com.ivantrykosh.app.zeitzuheiraten.presenter.main

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair
import com.ivantrykosh.app.zeitzuheiraten.utils.toStringDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onDateSelected: (DatePair) -> Unit,
    onDismiss: () -> Unit,
    selectableDates: BookingSelectableDates
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = selectableDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = datePickerState.selectedDateMillis != null,
                onClick = {
                    onDateSelected(
                        DatePair(
                            datePickerState.selectedDateMillis!!,
                            datePickerState.selectedDateMillis!!
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
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = stringResource(R.string.select_date),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                )
            },
            headline = {
                Text(
                    text = datePickerState.selectedDateMillis?.toStringDate() ?: "",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp)
                )
            },
            showModeToggle = false,
            modifier = Modifier.height(500.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DatePickerPreview() {
    DatePicker(
        onDateSelected = { },
        onDismiss = { },
        selectableDates = BookingSelectableDates(listOf(DatePair(1746392400000, 1746392400000)))
    )
}