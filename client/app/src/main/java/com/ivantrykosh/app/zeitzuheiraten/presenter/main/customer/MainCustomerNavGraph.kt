package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ivantrykosh.app.zeitzuheiraten.presenter.Screen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.home_screen.HomeScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_profile_screen.MyProfileScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.post.FullPostScreen

@Composable
fun MainCustomerNavGraph(navController: NavHostController, navigateToAuth: () -> Unit) {
    NavHost(navController = navController, startDestination = Screen.MainCustomerScreen.HomeScreen.route) {
        composable(route = Screen.MainCustomerScreen.HomeScreen.route) {
            HomeScreen(
                navigateToPost = { postId ->
                    navController.navigate(Screen.MainCustomerScreen.FullPostScreen.route + "?postId=$postId")
                }
            )
        }
        composable(route = Screen.MainCustomerScreen.MyProfileScreen.route) {
            MyProfileScreen {
                navigateToAuth()
            }
        }
        composable(
            route = Screen.MainCustomerScreen.FullPostScreen.route + "?postId={postId}",
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            FullPostScreen(
                postId = it.arguments!!.getString("postId")!!,
                navigateBack = { navController.popBackStack() },
                onProviderClicked = { /* todo */},
                onOpenChatClicked = { /* todo */ }
            )
        }
    }
}