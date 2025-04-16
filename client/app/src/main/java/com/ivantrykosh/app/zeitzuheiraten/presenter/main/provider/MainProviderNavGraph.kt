package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ivantrykosh.app.zeitzuheiraten.presenter.Screen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.add_post_screen.AddPostScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.bookings.BookingsScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.edit_post_screen.EditPostScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.home_screen.HomeScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider.my_profile_screen.MyProfileScreen

@Composable
fun MainProviderNavGraph(navController: NavHostController, navigateToAuth: () -> Unit) {
    NavHost(navController = navController, startDestination = Screen.MainProviderScreen.HomeScreen.route) {
        composable(route = Screen.MainProviderScreen.HomeScreen.route) {
            HomeScreen(
                navigateToAddPostScreen = {
                    navController.navigate(Screen.MainProviderScreen.AddPostScreen.route)
                },
                navigateToEditPostScreen = { postId ->
                    navController.navigate(Screen.MainProviderScreen.EditPostScreen.route + "?postId=$postId")
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
        composable(
            route = Screen.MainProviderScreen.EditPostScreen.route + "?postId={postId}",
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            EditPostScreen(postId = it.arguments!!.getString("postId")!!) {
                navController.popBackStack()
            }
        }
        composable(route = Screen.MainProviderScreen.BookingsScreen.route) {
            BookingsScreen(
                navigateToEditPost = { postId ->
                    navController.navigate(Screen.MainProviderScreen.EditPostScreen.route + "?postId=$postId")
                }
            )
        }
    }
}