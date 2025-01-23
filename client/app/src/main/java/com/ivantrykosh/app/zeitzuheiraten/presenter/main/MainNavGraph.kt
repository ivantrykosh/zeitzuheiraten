package com.ivantrykosh.app.zeitzuheiraten.presenter.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ivantrykosh.app.zeitzuheiraten.presenter.Screen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.home_screen.HomeScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.my_profile_screen.MyProfileScreen

@Composable
fun MainNavGraph(navController: NavHostController, navigateToAuth: () -> Unit) {
    NavHost(navController = navController, startDestination = Screen.MainScreen.HomeScreen.route) {
        composable(route = Screen.MainScreen.HomeScreen.route) {
            HomeScreen()
        }
        composable(route = Screen.MainScreen.MyProfileScreen.route) {
            MyProfileScreen {
                navigateToAuth()
            }
        }
    }
}