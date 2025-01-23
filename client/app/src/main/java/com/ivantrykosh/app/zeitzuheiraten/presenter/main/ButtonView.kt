package com.ivantrykosh.app.zeitzuheiraten.presenter.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R

@Composable
fun ButtonView(
    onClick: () -> Unit,
    @DrawableRes iconRes: Int,
    @StringRes title: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = stringResource(id = title), modifier = Modifier.size(25.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = stringResource(id = title), fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewButtonView() {
    ButtonView(
        onClick = { },
        iconRes = R.drawable.outline_account_circle_24,
        title = R.string.my_profile
    )
}