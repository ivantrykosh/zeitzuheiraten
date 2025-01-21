package com.ivantrykosh.app.zeitzuheiraten.presenter

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ivantrykosh.app.zeitzuheiraten.presenter.auth.auth_main_screen.AuthMainScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.auth.sign_in_screen.SignInScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.auth.sign_up_screen.SignUpScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.MainScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.splash_screen.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(
                navigateToAuthPage = {
                    navController.navigate(Screen.AuthScreen.AuthMainScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                },
                navigateToMainPage = {
                    navController.navigate(Screen.MainScreen.MainScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.AuthScreen.AuthMainScreen.route) {
            AuthMainScreen(
                navigateToSignInPage = {
                    navController.navigate(Screen.AuthScreen.SignInScreen.route)
                },
                navigateToSignUpPage = {
                    navController.navigate(Screen.AuthScreen.SignUpScreen.route)
                }
            )
        }
        composable(route = Screen.AuthScreen.SignUpScreen.route) {
            SignUpScreen {
                navController.navigate(Screen.MainScreen.MainScreen.route) {
                    popUpTo(Screen.AuthScreen.AuthMainScreen.route) { inclusive = true }
                }
            }
        }
        composable(route = Screen.AuthScreen.SignInScreen.route) {
            SignInScreen {
                navController.navigate(Screen.MainScreen.MainScreen.route) {
                    popUpTo(Screen.AuthScreen.AuthMainScreen.route) { inclusive = true }
                }
            }
        }
        composable(route = Screen.MainScreen.MainScreen.route) {
            MainScreen()
        }
    }
}