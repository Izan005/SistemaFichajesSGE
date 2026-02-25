package com.example.sistemafichajessge.ui.newTask

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.sistemafichajessge.data.dbSingleton.FichajesApplication
import com.example.sistemafichajessge.data.model.Department
import com.example.sistemafichajessge.data.model.Task
import com.example.sistemafichajessge.data.model.User
import com.example.sistemafichajessge.data.repository.DepartmentRepository
import com.example.sistemafichajessge.data.repository.TaskRepository
import com.example.sistemafichajessge.data.repository.UserRepository
import com.example.sistemafichajessge.ui.task.TaskScreen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewTaskViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    private val repo: TaskRepository = (application as FichajesApplication).taskRepo

    private val repoDep: DepartmentRepository = (application as FichajesApplication).departmentRepo

    private val repoUser: UserRepository = (application as FichajesApplication).userRepo

    private val userId: Int = checkNotNull(savedStateHandle[TaskScreen.userIdArg])


    val userRecieved: StateFlow<User?> = repoUser.getUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    val departmentList: StateFlow<List<Department>> = repoDep.getAllDepartments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    fun insertTask(task: Task) {
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

    fun updateTask(task: Task) {
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

    fun deleteTask(task: Task) {
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

    fun deleteTaskById(id: Int) {
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

    fun getTaskById(id: Int): StateFlow<Task?> {
        return repo.getById(id).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null
        )
    }

}