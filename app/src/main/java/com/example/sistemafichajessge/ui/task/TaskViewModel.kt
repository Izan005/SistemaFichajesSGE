package com.example.sistemafichajessge.ui.task

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.sistemafichajessge.data.dbSingleton.FichajesApplication
import com.example.sistemafichajessge.data.model.Task
import com.example.sistemafichajessge.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: TaskRepository = (application as FichajesApplication).taskRepo

    val taskList = repo.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        emptyList()
    )

    fun insert(task: Task) {
        viewModelScope.launch {
            try {
                repo.insert(task)
            } catch (e: Exception) {
                Toast.makeText(
                    application,
                    "Insert fallido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun update(task: Task) {
        viewModelScope.launch {
            try {
                repo.update(task)
            } catch (e: Exception) {
                Toast.makeText(
                    application,
                    "Update fallido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            try {
                repo.delete(task)
            } catch (e: Exception) {
                Toast.makeText(
                    application,
                    "Insert fallido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch {
            try {
                repo.deleteById(id)
            } catch (e: Exception) {
                Toast.makeText(
                    application,
                    "Delete fallido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getById(id: Int): StateFlow<Task?> {
        return repo.getById(id).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null
        )
    }


}