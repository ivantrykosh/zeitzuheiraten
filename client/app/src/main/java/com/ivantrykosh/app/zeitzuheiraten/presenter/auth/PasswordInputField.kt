package com.ivantrykosh.app.zeitzuheiraten.presenter.auth

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivantrykosh.app.zeitzuheiraten.R

@Composable
fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChange: (FocusState) -> Unit,
    error: Boolean,
    errorMessage: String
) {
    var showPassword by remember { mutableStateOf(false) }
    val visualTransformation: VisualTransformation
    @DrawableRes val showPasswordIcon: Int
    @StringRes val showPasswordString: Int
    if (showPassword) {
        visualTransformation = VisualTransformation.None
        showPasswordIcon = R.drawable.baseline_visibility_24
        showPasswordString = R.string.show_password
    } else {
        visualTransformation = PasswordVisualTransformation()
        showPasswordIcon = R.drawable.baseline_visibility_off_24
        showPasswordString = R.string.hide_password
    }

    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(text = stringResource(id = R.string.password)) },
        singleLine = true,
        visualTransformation = visualTransformation,
        leadingIcon = {
            Icon(painter = painterResource(id = R.drawable.baseline_password_24), contentDescription = stringResource(id = R.string.password_icon))
        },
        trailingIcon = {
            Icon(painter = painterResource(id = showPasswordIcon), contentDescription = stringResource(id = showPasswordString), modifier = Modifier.clickable { showPassword = !showPassword })
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
fun PreviewPasswordInputField() {
    PasswordInputField(
        value = "",
        onValueChange = {},
        onFocusChange = {},
        error = false,
        errorMessage = ""
    )
}