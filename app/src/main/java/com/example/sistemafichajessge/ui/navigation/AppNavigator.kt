package com.example.sistemafichajessge.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sistemafichajessge.ui.home.HomeScreen
import com.example.sistemafichajessge.ui.login.LoginScreen

@Composable
fun AppNavigation(
    navController : NavHostController,
    modifier: Modifier = Modifier
){


    NavHost(
        navController = navController,
        startDestination = LoginScreen.route,
        modifier = modifier
    ){
        composable(route = LoginScreen.route) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate("${HomeScreen.route}/$it")
                },
            )
        }

        composable(
            route = HomeScreen.routeWithArgs,
            arguments = listOf(
                navArgument(HomeScreen.userIdArg){
                    type = NavType.IntType
                }
            )) {
            HomeScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}