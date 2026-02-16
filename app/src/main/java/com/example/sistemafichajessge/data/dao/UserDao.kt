package com.example.sistemafichajessge.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sistemafichajessge.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM USER WHERE id = :id")
    fun getUser(id: Int): Flow<User>

    @Query("SELECT * FROM USER")
    fun getAllUsers(): Flow<List<User>>
}