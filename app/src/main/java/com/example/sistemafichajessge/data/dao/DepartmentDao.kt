package com.example.sistemafichajessge.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sistemafichajessge.data.model.Department
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {

    @Insert
    suspend fun insert(department: Department)

    @Update
    suspend fun update(department: Department)

    @Delete
    suspend fun delete(department: Department)

    @Query("SELECT * FROM DEPARTMENT WHERE id = :id")
    fun getDepartment(id: Int): Flow<Department>

    @Query("SELECT * FROM DEPARTMENT")
    fun getAllDepartments(): Flow<List<Department>>
}