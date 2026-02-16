package com.example.sistemafichajessge.ui.login

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemafichajessge.FichajesViewModelProvider
import com.example.sistemafichajessge.data.model.Registry
import com.example.sistemafichajessge.data.model.User
import com.example.sistemafichajessge.ui.navigation.FichajesNavDestination
import kotlinx.coroutines.launch


object LoginScreen : FichajesNavDestination {

    override val route = "login"

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    navigateToHome: (Int) -> Unit,
    viewModel: LoginViewModel = viewModel(factory = FichajesViewModelProvider.Factory),
    modifier: Modifier = Modifier,
) {
    var currentUser by remember { mutableStateOf<User?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                //.padding(paddingValues) // Aplicamos el padding aquí
                .fillMaxSize()
        ) {
            LoginCard(
                viewModel = viewModel,
                navigateToHome = navigateToHome,
                onUserVerified = { user ->
                    currentUser = user
                    viewModel.updateButtonStates(user.id)
                }
            )

            if (viewModel.isVerified.value && currentUser != null) {
                val buttons = listOf(
                    Triple("Fichar Entrada", "entry", viewModel.canEntry.value),
                    Triple("Fichar Salida", "exit", viewModel.canExit.value),
                    Triple("Entrada Descanso", "break_start", viewModel.canBreakStart.value),
                    Triple("Salida Descanso", "break_end", viewModel.canBreakEnd.value)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(buttons) { (label, type, isEnabled) ->
                        FichajeCard(
                            text = label,
                            enabled = isEnabled,
                            onClick = {
                                currentUser?.let { user ->
                                    coroutineScope.launch {
                                        val newRegistry = Registry(
                                            id = 0,
                                            userId = user.id,
                                            dateRegistry = System.currentTimeMillis(),
                                            type = type
                                        )
                                        viewModel.createRegistry(newRegistry)
                                        viewModel.updateButtonStates(userId = user.id)

                                        // 3. Mostramos el Snackbar desde aquí
                                        snackbarHostState.currentSnackbarData?.dismiss()

                                        if (viewModel.itsOvertime()){
                                            snackbarHostState.showSnackbar(
                                                message = "Marcado como horas extra por estár fuera de horario",
                                                duration = SnackbarDuration.Short
                                            )
                                        }

                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginCard(
    viewModel: LoginViewModel,
    navigateToHome: (Int) -> Unit,
    onUserVerified: (User) -> Unit
) {

    val userList by viewModel.userList.collectAsStateWithLifecycle()

    val departmentList by viewModel.departmentList.collectAsStateWithLifecycle()

    var expandedDepartment by remember { mutableStateOf(false) }

    var expandedUser by remember { mutableStateOf(false) }

    val defaultUser = User(
        id = -1,
        name = "Selecciona Usuario",
        departmentId = -1,
        pass = "",
        job = ""
    )

    var selectedUser by remember {mutableStateOf(defaultUser)}

    // Este estado depende de userList → NO usar rememberSaveable aquí
    var selectedDepartment by remember(departmentList) {
        mutableStateOf(departmentList.firstOrNull())
    }

    var showPassCard by rememberSaveable { mutableStateOf(false) }

    var passFailed by rememberSaveable { mutableStateOf(false) }

    var pass by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(selectedUser, selectedDepartment) {
        viewModel.isVerified.value = false
        showPassCard = false
        passFailed = false
        pass = ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Iniciar Sesión",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            ExposedDropdownMenuBox(
                expanded = expandedDepartment,
                onExpandedChange = { expandedDepartment = !expandedDepartment },

            ) {

                OutlinedTextField(
                    value = selectedDepartment?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar departamento") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDepartment)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedDepartment,
                    onDismissRequest = { expandedDepartment = false },

                ) {

                    departmentList.forEach { department ->
                        DropdownMenuItem(
                            text = { Text(department.name) },
                            onClick = {
                                selectedDepartment = department
                                expandedDepartment = false
                                selectedUser = defaultUser
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedUser,
                onExpandedChange = { expandedUser = !expandedUser }
            ) {

                OutlinedTextField(
                    value = selectedUser?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar usuario") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUser)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedUser,
                    onDismissRequest = { expandedUser = false }
                ) {

                    userList.forEach { user ->

                        if (user.departmentId == selectedDepartment?.id)
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                selectedUser = user
                                expandedUser = false
                            }
                        )
                    }
                }
            }

            if (viewModel.isVerified.value){
                Button(
                    onClick = { navigateToHome(selectedUser?.id ?: 0) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C3E50)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    enabled = if (selectedUser.name == defaultUser.name) false else true
                ) {

                    Text(
                        text = "Ver Detalles del Usuario",
                        color = if (selectedUser.name == defaultUser.name) Color.Gray else Color.White
                    )
                }
            }

            Button(
                onClick = { showPassCard = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C3E50)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                enabled = if (!viewModel.isVerified.value && selectedUser.name != defaultUser.name) true else false
            ) {

                Text(
                    text = if (!viewModel.isVerified.value) "Verificar" else "Usuario Verificado",
                    color = if (!viewModel.isVerified.value && selectedUser.name != defaultUser.name) Color.White else Color.Gray
                )
            }

            if (showPassCard && selectedUser.name != defaultUser.name){

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Introduce tu contraseña",
                            style = MaterialTheme.typography.titleSmall,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = pass,
                            onValueChange = { pass = it },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        if (passFailed){
                            Text(
                                text ="Contraseña incorrecta",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Red,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (selectedUser.pass == pass)
                                { viewModel.isVerified.value = true

                                    showPassCard = false
                                    passFailed = false

                                    onUserVerified(selectedUser)
                                }
                                else{
                                    passFailed = true
                                }},
                            modifier = Modifier.fillMaxWidth(),
                            enabled = if (!pass.isEmpty()) true else false
                        ) {
                            Text("Aceptar")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

            }
        }
    }
}

@Composable
fun FichajeCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean,
) {
    Card(
        onClick = { if (enabled) onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant
            else Color.LightGray.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(if (enabled) 6.dp else 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

