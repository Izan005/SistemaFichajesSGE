package com.example.sistemafichajessge.ui.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.BackspaceCommand
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemafichajessge.ui.home.HomeScreen
import com.example.sistemafichajessge.ui.navigation.FichajesNavDestination

object TaskScreen : FichajesNavDestination {
    override val route = "task"

    const val userIdArg = "id_user"

    val routeWithArgs = "${TaskScreen.route}/{$userIdArg}"
}

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: TaskViewModel = viewModel()
    ) {

    val taskList by viewModel.taskList.collectAsStateWithLifecycle()




}