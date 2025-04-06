package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ivantrykosh.app.zeitzuheiraten.presenter.Screen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.home_screen.HomeScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_profile_screen.MyProfileScreen

@Composable
fun MainCustomerNavGraph(navController: NavHostController, navigateToAuth: () -> Unit) {
    NavHost(navController = navController, startDestination = Screen.MainCustomerScreen.HomeScreen.route) {
        composable(route = Screen.MainCustomerScreen.HomeScreen.route) {
            HomeScreen()
        }
        composable(route = Screen.MainCustomerScreen.MyProfileScreen.route) {
            MyProfileScreen {
                navigateToAuth()
            }
        }
    }
}