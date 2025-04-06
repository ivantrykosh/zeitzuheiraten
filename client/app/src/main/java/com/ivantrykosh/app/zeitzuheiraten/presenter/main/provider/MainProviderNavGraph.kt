package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ivantrykosh.app.zeitzuheiraten.presenter.Screen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.add_post_screen.AddPostScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.home_screen.HomeScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.my_profile_screen.MyProfileScreen

@Composable
fun MainProviderNavGraph(navController: NavHostController, navigateToAuth: () -> Unit) {
    NavHost(navController = navController, startDestination = Screen.MainProviderScreen.HomeScreen.route) {
        composable(route = Screen.MainProviderScreen.HomeScreen.route) {
            HomeScreen(
                navigateToAddPostScreen = {
                    navController.navigate(Screen.MainProviderScreen.AddPostScreen.route)
                }
            )
        }
        composable(route = Screen.MainProviderScreen.MyProfileScreen.route) {
            MyProfileScreen {
                navigateToAuth()
            }
        }
        composable(route = Screen.MainProviderScreen.AddPostScreen.route) {
            AddPostScreen {
                navController.popBackStack()
            }
        }
    }
}