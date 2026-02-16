package com.example.sistemafichajessge

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sistemafichajessge.data.dbSingleton.FichajesApplication
import com.example.sistemafichajessge.ui.home.HomeViewModel
import com.example.sistemafichajessge.ui.login.LoginViewModel

object FichajesViewModelProvider {

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


    }
}

/**
 * Función de extensión para extraer el objeto FichajesApplication
 * de los extras de creación del ViewModel.
 */
fun CreationExtras.fichajesApplication(): FichajesApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FichajesApplication)