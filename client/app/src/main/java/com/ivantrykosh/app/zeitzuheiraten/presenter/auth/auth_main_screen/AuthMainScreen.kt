package com.ivantrykosh.app.zeitzuheiraten.presenter.auth.auth_main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivantrykosh.app.zeitzuheiraten.R

@Preview(showBackground = true)
@Composable
fun AuthMainScreen(
    navigateToSignInPage: () -> Unit = { },
    navigateToSignUpPage: () -> Unit = { }
) {
    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_icon),
                modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally).scale(3f),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 64.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                lineHeight = 70.sp,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            )
        }
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 100.dp)
                .padding(horizontal = 50.dp)
                .align(Alignment.BottomCenter),
        ) {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                navigateToSignInPage()
            }) {
                Text(text = stringResource(id = R.string.sign_in_title))
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(12.dp))
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                navigateToSignUpPage()
            }) {
                Text(text = stringResource(id = R.string.sign_up_title))
            }
        }
    }
}