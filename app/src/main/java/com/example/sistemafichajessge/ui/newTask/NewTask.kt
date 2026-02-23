package com.example.sistemafichajessge.ui.newTask

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemafichajessge.ui.navigation.FichajesNavDestination
import com.example.sistemafichajessge.ui.task.TaskViewModel

object NewTaskScreen : FichajesNavDestination {
    override val route = "new_task"
}

@Composable
fun NewTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel()
    ) {



}