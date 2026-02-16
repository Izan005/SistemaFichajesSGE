package com.example.sistemafichajessge.data.repository

import com.example.sistemafichajessge.data.dao.UserDao
import com.example.sistemafichajessge.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao){

     suspend fun insert(user: User) {
        dao.insert(user)
    }

     suspend fun update(user: User) {
        dao.update(user)
    }

     suspend fun delete(user: User) {
        dao.delete(user)
    }

     fun getUser(id: Int): Flow<User> {
        return dao.getUser(id)
    }

     fun getAllUsers(): Flow<List<User>> {
        return dao.getAllUsers()
    }
}