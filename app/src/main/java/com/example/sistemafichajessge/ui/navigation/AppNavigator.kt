package com.example.sistemafichajessge.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sistemafichajessge.ui.home.HomeScreen
import com.example.sistemafichajessge.ui.login.LoginScreen
import com.example.sistemafichajessge.ui.newTask.NewTaskScreen
import com.example.sistemafichajessge.ui.task.TaskScreen

@RequiresApi(Build.VERSION_CODES.O)
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
                navigateBack = { navController.popBackStack() },
                navigateToTasks = { navController.navigate("${TaskScreen.route}/$it")}
            )
        }

        composable(
            route = TaskScreen.routeWithArgs,
            arguments = listOf(
                navArgument(TaskScreen.userIdArg){
                    type = NavType.IntType
                }
            )
        ) {
            TaskScreen(
                navigateBack = { navController.popBackStack() },
                navigateToCreateTask = { navController.navigate(NewTaskScreen.route)}
            )
        }

        composable(route = NewTaskScreen.route){
            NewTaskScreen()
        }
    }
}