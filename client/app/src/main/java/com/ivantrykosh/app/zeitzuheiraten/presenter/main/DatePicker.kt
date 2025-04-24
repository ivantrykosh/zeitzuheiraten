package com.ivantrykosh.app.zeitzuheiraten.presenter.main

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.domain.model.DatePair

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onDateSelected: (DatePair) -> Unit,
    onDismiss: () -> Unit,
    selectableDates: BookingSelectableDates = BookingSelectableDates(emptyList())
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
                    text =stringResource(R.string.select_date)
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