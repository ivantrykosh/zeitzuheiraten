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
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.chats.ChatsScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.chats.chat.ChatScreen
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.shared.profile.ProfileScreen

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
                onProviderClicked = { navController.navigate(Screen.MainCustomerScreen.ProfileScreen.route + "?userId=$it") },
                onOpenChatClicked = { userId, username ->
                    navController.navigate(Screen.MainCustomerScreen.ChatScreen.route + "?chatId=${null}&userId=$userId&username=$username")
                },
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
            MyBookingsScreen(
                navigateToPost = { postId ->
                    navController.navigate(Screen.MainCustomerScreen.FullPostScreen.route + "?postId=$postId")
                },
                navigateToUser = { userId ->
                    navController.navigate(Screen.MainCustomerScreen.ProfileScreen.route + "?userId=$userId")
                },
            )
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
                },
                navigateToUser = { userId ->
                    navController.navigate(Screen.MainCustomerScreen.ProfileScreen.route + "?userId=$userId")
                },
            )
        }
        composable(route = Screen.MainCustomerScreen.MyFeedbacksScreen.route) {
            MyFeedbacksScreen(
                navigateBack = { navController.popBackStack() },
                navigateToPost = { postId ->
                    navController.navigate(Screen.MainCustomerScreen.FullPostScreen.route + "?postId=$postId")
                },
            )
        }
        composable(route = Screen.MainCustomerScreen.ChatsScreen.route) {
            ChatsScreen(
                navigateToChat = { chatId, userId, username ->
                    navController.navigate(Screen.MainCustomerScreen.ChatScreen.route + "?chatId=$chatId&userId=$userId&username=$username")
                }
            )
        }
        composable(
            route = Screen.MainCustomerScreen.ChatScreen.route + "?chatId={chatId}&userId={userId}&username={username}",
            arguments = listOf(
                navArgument("chatId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("userId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("username") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            ChatScreen(
                chatId = it.arguments!!.getString("chatId"),
                withUserId = it.arguments!!.getString("userId")!!,
                withUserName = it.arguments!!.getString("username")!!,
                navigateToUser = { userId ->
                    navController.navigate(Screen.MainCustomerScreen.ProfileScreen.route + "?userId=$userId")
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.MainCustomerScreen.ProfileScreen.route + "?userId={userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            val userId = it.arguments!!.getString("userId")!!
            ProfileScreen(
                userId = userId,
                navigateBack = { navController.popBackStack() },
                onOpenChatClicked = { idOfUser, username ->
                    navController.navigate(Screen.MainCustomerScreen.ChatScreen.route + "?chatId=${null}&userId=$idOfUser&username=$username")
                },
            )
        }
    }
}