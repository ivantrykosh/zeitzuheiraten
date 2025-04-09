package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R

@Composable
fun FilterItemInputNumber(
    currentValue: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffix: String
) {
    TextField(
        value = currentValue,
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        suffix = { Text(suffix, fontSize = 18.sp) },
        label = {
            Text(text = label, fontSize = 14.sp)
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
        modifier = modifier.height(75.dp),
    )
}

@Composable
@Preview(showBackground = true)
fun FilterItemInputNumberPreview() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        FilterItemInputNumber(
            currentValue = "0",
            onValueChange = { },
            label = stringResource(R.string.min_price),
            modifier = Modifier.fillMaxWidth(),
            suffix = "₴"
        )
    }
}