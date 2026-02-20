package com.example.sistemafichajessge.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Box(
        modifier = modifier.fillMaxSize()
    ){
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = taskList, key = { it.id }){ task ->

            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            CreateTaskButton()
        }
    }

}

@Composable
fun CreateTaskButton(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel()
){
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.background(color = Color(0xFF2C3E50), CircleShape)
                .clip(CircleShape)
                .size(80.dp)
                .padding(20.dp)
                .clickable {

                }
    ){
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Crear Tarea",
            tint = Color.White
        )
    }
}