package com.example.sistemafichajessge.ui.newTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemafichajessge.data.model.Department
import com.example.sistemafichajessge.data.model.Task
import com.example.sistemafichajessge.ui.navigation.FichajesNavDestination
import com.example.sistemafichajessge.ui.newTask.NewTaskScreen.userIdArg
import java.time.Instant
import java.util.Date

object NewTaskScreen : FichajesNavDestination {
    override val route = "new_task"

    const val userIdArg = "id_user"

    val routeWithArgs = "${NewTaskScreen.route}/{${NewTaskScreen.userIdArg}}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewTaskScreen(
    modifier: Modifier = Modifier,
    navigateToTaskScreen: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: NewTaskViewModel = viewModel(),
) {

    CreateTaskForm(
        navigateToTaskScreen = navigateToTaskScreen,
        navigateBack = navigateBack
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskForm(
    navigateToTaskScreen: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: NewTaskViewModel = viewModel()
) {
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var deptoSeleccionado by remember { mutableStateOf<Department?>(null) }
    var isAllDeptos by remember { mutableStateOf(false) }

    // Estado para el Dropdown
    var expanded by remember { mutableStateOf(false) }
    val departamentsList by viewModel.departmentList.collectAsStateWithLifecycle()

    // Usuario recibido como parámetro
    val user by viewModel.userRecieved.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.run {
            fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nueva Tarea", style = MaterialTheme.typography.headlineMedium, color = Color.White)

        // Campo: Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la tarea") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Campo: Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )

        // Campo: Dropdown Departamento
        Box {
            OutlinedTextField(
                value = deptoSeleccionado?.name ?: "Selecciona...",
                onValueChange = {},
                readOnly = true,
                label = { Text("Departamento Destino") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                departamentsList.forEach { depto ->
                    DropdownMenuItem(
                        text = { Text(depto.name) },
                        onClick = {

                            deptoSeleccionado = depto
                            expanded = false
                        }
                    )
                }
            }
        }

        // Campo: Radio Buttons (Dirigido a todos)
        Text("¿Enviar a todos los departamentos?", color = Color.White)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isAllDeptos,
                onClick = { isAllDeptos = true }
            )
            Text("Sí", color = Color.White, modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = !isAllDeptos,
                onClick = { isAllDeptos = false }
            )
            Text("No (Solo seleccionado)", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Envío
        Button(
            enabled =
            if (nombre.isEmpty() || descripcion.isEmpty() || deptoSeleccionado == null) false else true,
            onClick = {
                viewModel.insertTask(
                    Task(
                        nombre = nombre,
                        descripcion = descripcion,
                        timeStamp = Date.from(Instant.now()),
                        creador = user?.id ?: 0,
                        destinatario = null,
                        departDestino = deptoSeleccionado?.id ?: 0,
                        isAll = isAllDeptos
                    )
                )
                navigateToTaskScreen(user?.id ?: 0)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Text("CREAR TAREA")
        }
    }
}