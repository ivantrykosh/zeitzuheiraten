package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ivantrykosh.app.zeitzuheiraten.presenter.Screen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.budget_picker.BudgetPickerScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.budget_picker.BudgetPickerViewModel
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.budget_picker.posts_with_budget.PostsWithBudgetScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.feedbacks.FeedbackScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.home_screen.HomeScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_bookings.MyBookingsScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_feedbacks.MyFeedbacksScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.my_profile_screen.MyProfileScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.post.FullPostScreen

@Composable
fun MainCustomerNavGraph(navController: NavHostController, navigateToAuth: () -> Unit) {
    val budgetPickerViewModel: BudgetPickerViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = Screen.MainCustomerScreen.HomeScreen.route) {
        composable(route = Screen.MainCustomerScreen.HomeScreen.route) {
            HomeScreen(
                navigateToPost = { postId ->
                    navController.navigate(Screen.MainCustomerScreen.FullPostScreen.route + "?postId=$postId")
                }
            )
        }
        composable(route = Screen.MainCustomerScreen.MyProfileScreen.route) {
            MyProfileScreen(
                onSignOut = {
                    navigateToAuth()
                },
                navigateToMyFeedbacks = {
                    navController.navigate(Screen.MainCustomerScreen.MyFeedbacksScreen.route)
                }
            )
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
            val postId = it.arguments!!.getString("postId")!!
            FullPostScreen(
                postId = postId,
                navigateBack = { navController.popBackStack() },
                onProviderClicked = { /* todo */},
                onOpenChatClicked = { /* todo */ },
                navigateToPostFeedbacks = {
                    navController.navigate(Screen.MainCustomerScreen.PostFeedbacksScreen.route + "?postId=$postId")
                }
            )
        }
        composable(route = Screen.MainCustomerScreen.BudgetPickerScreen.route) {
            BudgetPickerScreen(
                budgetPickerViewModel = budgetPickerViewModel,
                navigateToPosts = {
                    navController.navigate(Screen.MainCustomerScreen.PostsWithBudgetScreen.route)
                }
            )
        }
        composable(route = Screen.MainCustomerScreen.PostsWithBudgetScreen.route) {
            PostsWithBudgetScreen(
                budgetPickerViewModel = budgetPickerViewModel,
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToPost = { postId ->
                    navController.navigate(Screen.MainCustomerScreen.FullPostScreen.route + "?postId=$postId")
                }
            )
        }
        composable(route = Screen.MainCustomerScreen.MyBookingsScreen.route) {
            MyBookingsScreen { postId ->
                navController.navigate(Screen.MainCustomerScreen.FullPostScreen.route + "?postId=$postId")
            }
        }
        composable(
            route = Screen.MainCustomerScreen.PostFeedbacksScreen.route + "?postId={postId}",
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            FeedbackScreen(
                postId = it.arguments!!.getString("postId")!!,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.MainCustomerScreen.MyFeedbacksScreen.route) {
            MyFeedbacksScreen {
                navController.popBackStack()
            }
        }
    }
}