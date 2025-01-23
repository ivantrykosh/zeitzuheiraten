package com.ivantrykosh.app.zeitzuheiraten.presenter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivantrykosh.app.zeitzuheiraten.R

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    @DrawableRes icon: Int,
    @StringRes iconDescription: Int,
    onFocusChange: (FocusState) -> Unit,
    error: Boolean,
    errorMessage: String
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(text = stringResource(id = label)) },
        singleLine = true,
        leadingIcon = {
            Icon(painter = painterResource(id = icon), contentDescription = stringResource(id = iconDescription))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .onFocusChanged { onFocusChange(it) },
        shape = RoundedCornerShape(50f),
        supportingText = { Text(text = errorMessage) },
        isError = error,
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInputField() {
    InputField(
        value = "",
        onValueChange = {},
        label = R.string.email,
        icon = R.drawable.baseline_email_24,
        iconDescription = R.string.email_icon,
        onFocusChange = {},
        error = false,
        errorMessage = ""
    )
}