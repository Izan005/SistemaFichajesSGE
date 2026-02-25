package com.example.sistemafichajessge.ui.task

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemafichajessge.data.model.Task
import com.example.sistemafichajessge.data.model.User
import com.example.sistemafichajessge.ui.navigation.FichajesNavDestination
import com.example.sistemafichajessge.ui.newTask.NewTaskScreen.userIdArg
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.Locale

object TaskScreen : FichajesNavDestination {
    override val route = "task"

    const val userIdArg = "id_user"

    val routeWithArgs = "${TaskScreen.route}/{$userIdArg}"
}

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToCreateTask: (Int) -> Unit,
    viewModel: TaskViewModel = viewModel()
    ) {

    val taskList by viewModel.taskList.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize()
    ){
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = taskList, key = { it.id }){ task ->
                TaskCard(
                    task = task,
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            CreateTaskButton(
                navigateToCreateTask = navigateToCreateTask
            )
        }
    }

}

@Composable
fun CreateTaskButton(
    modifier: Modifier = Modifier,
    navigateToCreateTask: (Int) -> Unit,
    viewModel: TaskViewModel = viewModel()
){

    val user by viewModel.userRecieved.collectAsStateWithLifecycle()

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.background(color = Color(0xFF2C3E50), CircleShape)
                .clip(CircleShape)
                .size(80.dp)
                .padding(20.dp)
                .clickable {
                    navigateToCreateTask(user?.id ?: 0)
                }
    ){
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Crear Tarea",
            tint = Color.White,
            modifier = Modifier.clip(CircleShape)
        )
    }
}


@Composable
fun TaskCard(
    task: Task,
    viewModel: TaskViewModel = viewModel()
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val department by viewModel.searchDepartment(task.departDestino).collectAsStateWithLifecycle()

    // Estado para controlar la expansión
    var expanded by remember { mutableStateOf(false) }

    // User recivido como parámetro
    val user by viewModel.userRecieved.collectAsStateWithLifecycle()
// Declaras la variable de estado vacía al principio
    var userDestinatario by remember { mutableStateOf<User?>(null) }

// Usas un LaunchedEffect para lanzar la búsqueda cuando el ID cambie
    LaunchedEffect(task.destinatario) {
        task.destinatario?.let { id ->
            // Aquí llamas a la función (que debería ser suspend o devolver el valor)
            val user = viewModel.getUserDestination(id)
            userDestinatario = user
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(), // Animación automática al cambiar el tamaño
        onClick = { expanded = !expanded }, // Alternar expansión al pulsar
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1C23)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // --- Cabecera: Título y Badge ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.nombre,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(task.estado)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Descripción (Expandible) ---
            Text(
                text = task.descripcion,
                color = Color(0xFF95A1AC),
                fontSize = 14.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 2 // Muestra todo si está expandido
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF2D3139), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // --- Footer: Depto y Fecha ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Depto: ${department?.name ?: "Cargando..."}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    text = dateFormatter.format(task.timeStamp),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // -- Usuario asignado a la tarea --
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Empleado Asignado: ${userDestinatario?.name ?: "Ninguno"}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    text = dateFormatter.format(task.timeStamp),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            // --- Sección Expandida: Botones de Acción ---
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))

                // Botón principal: Asignarse tarea
                Button(
                    onClick = { viewModel.update(task = task.copy(destinatario = user!!.id)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ASIGNARME ESTA TAREA", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Cambiar estado:", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))

                // Paquete de 3 botones de estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val estados = listOf("Hacer", "Haciendo", "Hecho")
                    estados.forEach { estado ->
                        OutlinedButton(
                            onClick = { viewModel.update(task = task.copy(estado = estado)) },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color(0xFF2D3139)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(estado, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(estado: String) {
    val color = when (estado.lowercase()) {
        "hacer" -> Color(0xFFC5A059) // Dorado/Ocre
        "progreso" -> Color(0xFF3498DB) // Azul
        else -> Color(0xFF2ECC71) // Verde
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = estado.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}