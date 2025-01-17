package com.ivantrykosh.app.zeitzuheiraten.presenter

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ivantrykosh.app.zeitzuheiraten.presenter.auth.sign_up_screen.SignUpScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.auth.verify_email.VerifyEmailScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(
                navigateToAuthPage = {
                    navController.navigate(Screen.AuthScreen.SignUpScreen.route) { // todo change sign up screen to greeting screen (with login and sign up buttons)
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                },
                navigateToMainPage = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.AuthScreen.SignUpScreen.route) {
            SignUpScreen {
                navController.navigate(Screen.AuthScreen.VerifyEmailScreen.route)
            }
        }
        composable(route = Screen.AuthScreen.VerifyEmailScreen.route) {
            VerifyEmailScreen()
        }
        composable(route = Screen.MainScreen.route) {
            MainScreen()
        }
    }
}