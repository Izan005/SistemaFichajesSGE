package com.example.sistemafichajessge.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sistemafichajessge.data.model.Registry
import com.example.sistemafichajessge.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistryDao {

    @Insert
    suspend fun insert(registry: Registry)

    @Update
    suspend fun update(registry: Registry)

    @Delete
    suspend fun delete(registry: Registry)

    @Query("SELECT * FROM REGISTRY WHERE id = :id")
    fun getRegistry(id: Int): Flow<Registry>

    @Query("SELECT * FROM REGISTRY")
    fun getAllRegistries(): Flow<List<Registry>>

    @Query("SELECT * FROM REGISTRY WHERE userId = :userId ORDER BY dateRegistry DESC")
    fun getRegistriesByUserId(userId: Int): Flow<List<Registry>>
}