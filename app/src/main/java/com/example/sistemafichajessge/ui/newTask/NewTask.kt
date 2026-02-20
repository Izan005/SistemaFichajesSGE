package com.example.sistemafichajessge.ui.newTask

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemafichajessge.ui.navigation.FichajesNavDestination
import com.example.sistemafichajessge.ui.task.TaskViewModel

object NewTask : FichajesNavDestination {
    override val route = "new_task"

    const val userIdArg = "id_user"

    val routeWithArgs = "${NewTask.route}/{$userIdArg}"
}

@Composable
fun NewTask(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel()
    ) {



}