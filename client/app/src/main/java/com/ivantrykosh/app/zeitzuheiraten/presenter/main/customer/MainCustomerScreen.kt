package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.Screen

@SuppressLint("RestrictedApi")
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCustomerScreen(
    navController: NavHostController = rememberNavController(),
    navigateToAuth: () -> Unit = { }
) {
    var selectedNavBarItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val homeNavBarItem = NavBarItem(title = R.string.home, selectedIcon = R.drawable.baseline_home_24, unselectedIcon = R.drawable.outline_home_24, navRoute = Screen.MainCustomerScreen.HomeScreen.route)
    val budgetPickerNavBarItem = NavBarItem(title = R.string.budget_picker, selectedIcon = R.drawable.baseline_savings_24, unselectedIcon = R.drawable.outline_savings_24, navRoute = Screen.MainCustomerScreen.BudgetPickerScreen.route)
    val myBookingsNavBarItem = NavBarItem(title = R.string.my_bookings, selectedIcon = R.drawable.baseline_book_24, unselectedIcon = R.drawable.outline_book_24, navRoute = Screen.MainCustomerScreen.MyBookingsScreen.route)
    val myProfileNavBarItem = NavBarItem(title = R.string.my_profile, selectedIcon = R.drawable.baseline_account_circle_24, unselectedIcon = R.drawable.outline_account_circle_24, navRoute = Screen.MainCustomerScreen.MyProfileScreen.route)
    val chatsNavBarItem = NavBarItem(title = R.string.chats, selectedIcon = R.drawable.baseline_chat_24, unselectedIcon = R.drawable.outline_chat_24, navRoute = Screen.MainCustomerScreen.ChatsScreen.route)
    val navBarItems = listOf(homeNavBarItem, budgetPickerNavBarItem, myBookingsNavBarItem, chatsNavBarItem, myProfileNavBarItem)

    navController.addOnDestinationChangedListener { _, destination, _ ->
        val index = navBarItems.indexOfFirst { it.navRoute == destination.route }
        var backStack = navController.currentBackStack.value
        if (index != -1) {
            selectedNavBarItemIndex = index
        } else if (backStack.indexOfLast { it.destination == destination } == backStack.lastIndex - 1) {
            backStack = backStack.dropLast(1)
            val dest = backStack.findLast { entry -> navBarItems.indexOfFirst { it.navRoute == entry.destination.route } != -1 }!!.destination
            selectedNavBarItemIndex = navBarItems.indexOfFirst { it.navRoute == dest.route }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navBarItems.forEachIndexed { index, navBarItem ->
                    val titleString = stringResource(id = navBarItem.title)
                    val isSelected = selectedNavBarItemIndex == index
                    val iconRes = if (isSelected) navBarItem.selectedIcon else navBarItem.unselectedIcon

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(navBarItem.navRoute) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(painter = painterResource(id = iconRes), contentDescription = titleString) },
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            MainCustomerNavGraph(navController = navController, navigateToAuth = navigateToAuth)
        }
    }
}

data class NavBarItem(
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val navRoute: String
)