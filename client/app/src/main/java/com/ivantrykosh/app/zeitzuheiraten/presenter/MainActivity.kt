package com.ivantrykosh.app.zeitzuheiraten.presenter

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.ivantrykosh.app.zeitzuheiraten.presenter.ui.theme.ZeitZuHeiratenTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ZeitZuHeiratenTheme(
                darkTheme = false,
                dynamicColor = false,
            ) {
                NavGraph(navController = navController)
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val locale = Locale("uk")
        val newConfig = Configuration(newBase?.resources?.configuration).apply {
            setLocale(locale)
        }
        super.attachBaseContext(newBase?.createConfigurationContext(newConfig))
    }
}