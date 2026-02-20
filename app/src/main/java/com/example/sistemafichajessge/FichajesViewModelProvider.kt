package com.example.sistemafichajessge

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sistemafichajessge.data.dbSingleton.FichajesApplication
import com.example.sistemafichajessge.ui.home.HomeViewModel
import com.example.sistemafichajessge.ui.login.LoginViewModel
import com.example.sistemafichajessge.ui.task.TaskViewModel

object FichajesViewModelProvider {

    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {

        initializer {

            val application = fichajesApplication()

            LoginViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                application = application
            )
        }

        initializer {
            // Obtenemos la instancia de la aplicación personalizada
            val application = fichajesApplication()

            HomeViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                application = application
            )
        }

        initializer {
            val application = fichajesApplication()

            TaskViewModel(
                application = application
            )
        }


    }
}

/**
 * Función de extensión para extraer el objeto FichajesApplication
 * de los extras de creación del ViewModel.
 */
fun CreationExtras.fichajesApplication(): FichajesApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FichajesApplication)