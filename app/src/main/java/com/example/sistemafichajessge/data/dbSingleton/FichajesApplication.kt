package com.example.sistemafichajessge.data.dbSingleton

import android.app.Application
import com.example.sistemafichajessge.data.repository.DepartmentRepository
import com.example.sistemafichajessge.data.repository.RegistryRepository
import com.example.sistemafichajessge.data.repository.TaskRepository
import com.example.sistemafichajessge.data.repository.UserRepository

class FichajesApplication : Application(){
    private val database by lazy { AppDatabase.getInstance(this) }

    val departmentRepo by lazy { DepartmentRepository(database.departmentDao()) }

    val userRepo by lazy { UserRepository(database.userDao()) }

    val registryRepo by lazy { RegistryRepository(database.registryDao()) }

    val taskRepo by lazy { TaskRepository(database.taskDao()) }

}