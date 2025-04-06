package com.ivantrykosh.app.zeitzuheiraten.presenter.main.provider

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainProviderScreen(
    navController: NavHostController = rememberNavController(),
    navigateToAuth: () -> Unit = { }
) {
    var selectedNavBarItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val homeNavBarItem = NavBarItem(title = R.string.home, selectedIcon = R.drawable.baseline_home_24, unselectedIcon = R.drawable.outline_home_24, navRoute = Screen.MainProviderScreen.HomeScreen.route)
    val myProfileNavBarItem = NavBarItem(title = R.string.my_profile, selectedIcon = R.drawable.baseline_account_circle_24, unselectedIcon = R.drawable.outline_account_circle_24, navRoute = Screen.MainProviderScreen.MyProfileScreen.route)
    val navBarItems = listOf(homeNavBarItem, myProfileNavBarItem)

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
                            selectedNavBarItemIndex = index
                            navController.navigate(navBarItem.navRoute) {
                                popUpTo(navController.currentDestination!!.route!!) {
                                    inclusive = true
                                }
                            }
                        },
                        icon = { Icon(painter = painterResource(id = iconRes), contentDescription = titleString) },
                        label = { Text(text = titleString) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            MainProviderNavGraph(navController = navController, navigateToAuth = navigateToAuth)
        }
    }
}

data class NavBarItem(
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val navRoute: String
)