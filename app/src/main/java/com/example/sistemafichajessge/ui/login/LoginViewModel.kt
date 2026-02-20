package com.example.sistemafichajessge.ui.login

import android.app.Application
import android.os.Build
import android.text.format.DateUtils
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.sistemafichajessge.data.dbSingleton.FichajesApplication
import com.example.sistemafichajessge.data.model.Department
import com.example.sistemafichajessge.data.model.Registry
import com.example.sistemafichajessge.data.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LoginViewModel(
    savedStateHandle: SavedStateHandle,
    application: Application
): AndroidViewModel(application)  {

    private val userRepository = (application as FichajesApplication).userRepo

    private val departmentRepository = (application as FichajesApplication).departmentRepo

    private val registryRepository = (application as FichajesApplication).registryRepo

    val userList: StateFlow<List<User>> = userRepository.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val departmentList: StateFlow<List<Department>> = departmentRepository.getAllDepartments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Estado de la app que verifica si el usuario está verificado
    var isVerified = mutableStateOf(false)

    // Estados de la app que definen si los botones relacionados con el fichaje están activos o no
    var canEntry = mutableStateOf(true)
    var canExit = mutableStateOf(false)
    var canBreakStart = mutableStateOf(false)
    var canBreakEnd = mutableStateOf(false)


    suspend fun createRegistry(registry: Registry){

        registryRepository.insert(registry)

    }

    fun updateButtonStates(userId: Int) {

        viewModelScope.launch {
            // Obtenemos el último registro
            val registries = registryRepository.getRegistriesByUserId(userId).first()
            val lastRegistry = registries.firstOrNull()

            if (lastRegistry == null || !DateUtils.isToday(lastRegistry.dateRegistry)) {
                // Si no hay registros, solo puede fichar entrada
                setButtons(entry = true, exit = false, bStart = false, bEnd = false)
            } else {
                // Lógica de estados
                when (lastRegistry.type) {
                    "entry" -> setButtons(entry = false, exit = true, bStart = true, bEnd = false)
                    "exit" -> setButtons(entry = true, exit = false, bStart = false, bEnd = false)
                    "break_start" -> setButtons(entry = false, exit = false, bStart = false, bEnd = true)
                    "break_end" -> setButtons(entry = false, exit = true, bStart = true, bEnd = false)
                    else -> setButtons(entry = true, exit = false, bStart = false, bEnd = false)
                }
            }
        }
    }

    private fun setButtons(entry: Boolean, exit: Boolean, bStart: Boolean, bEnd: Boolean) {
        canEntry.value = entry
        canExit.value = exit
        canBreakStart.value = bStart
        canBreakEnd.value = bEnd
    }



        @RequiresApi(Build.VERSION_CODES.O)
        fun itsOvertime(): Boolean {

            val ldt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis()),
                ZoneId.systemDefault()
            )

            if (ldt.hour < 9 || ldt.dayOfWeek.value.equals("MONDAY") || ldt.dayOfWeek.value.equals("FRIDAY")) {

                return true

            }

            return false
        }




}