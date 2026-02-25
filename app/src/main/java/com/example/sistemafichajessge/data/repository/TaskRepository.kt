package com.example.sistemafichajessge.data.repository

import com.example.sistemafichajessge.data.dao.TaskDao
import com.example.sistemafichajessge.data.model.Task
import com.example.sistemafichajessge.data.model.User
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {
    suspend fun insert(task: Task) {
        dao.insert(task)
    }

    suspend fun update(task: Task) {
        dao.update(task)
    }

    suspend fun delete(task: Task) {
        dao.delete(task)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    fun getAll(): Flow<List<Task>> {
        return dao.getAll()
    }

    fun getById(id: Int): Flow<Task> {
        return dao.getById(id)
    }

    fun getUserDestination(id: Int): Flow<User> {
        return dao.getUserDestination(id)
    }

}