package com.ivantrykosh.app.zeitzuheiraten.presenter

sealed class Screen(val route: String) {
    data object SplashScreen: Screen("splash_screen")
    sealed class AuthScreen(route: String): Screen(route) {
        data object AuthMainScreen: AuthScreen("auth_main_screen")
        data object SignUpScreen: AuthScreen("sign_up_screen")
        data object SignInScreen: AuthScreen("sign_in_screen")
    }
    sealed class MainCustomerScreen(route: String): Screen(route) {
        data object MainCustomerScreen: Screen.MainCustomerScreen("main_customer_screen")
        data object HomeScreen: Screen.MainCustomerScreen("home_customer_screen")
        data object MyProfileScreen: Screen.MainCustomerScreen("my_profile_customer_screen")
        data object FullPostScreen: Screen.MainCustomerScreen("full_post_screen")
        data object BudgetPickerScreen: Screen.MainCustomerScreen("budget_picker_screen")
        data object PostsWithBudgetScreen: Screen.MainCustomerScreen("posts_with_budget_screen")
        data object MyBookingsScreen: Screen.MainCustomerScreen("my_bookings_screen")
    }
    sealed class MainProviderScreen(route: String): Screen(route) {
        data object MainProviderScreen: Screen.MainProviderScreen("main_provider_screen")
        data object HomeScreen: Screen.MainProviderScreen("home_provider_screen")
        data object MyProfileScreen: Screen.MainProviderScreen("my_profile_provider_screen")
        data object AddPostScreen: Screen.MainProviderScreen("add_post_provider_screen")
        data object EditPostScreen: Screen.MainProviderScreen("edit_post_provider_screen")
    }
}