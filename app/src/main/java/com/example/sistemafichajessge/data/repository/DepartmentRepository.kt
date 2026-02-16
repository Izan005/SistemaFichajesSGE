package com.example.sistemafichajessge.data.repository

import com.example.sistemafichajessge.data.dao.DepartmentDao
import com.example.sistemafichajessge.data.model.Department
import kotlinx.coroutines.flow.Flow

class DepartmentRepository(private val departmentDao: DepartmentDao){
     suspend fun insert(department: Department) {
        departmentDao.insert(department)
    }

     suspend fun update(department: Department) {
         departmentDao.update(department)
    }

     suspend fun delete(department: Department) {
        departmentDao.delete(department)
    }

     fun getDepartment(id: Int): Flow<Department> {
       return departmentDao.getDepartment(id)
    }

     fun getAllDepartments(): Flow<List<Department>> {
        return departmentDao.getAllDepartments()
    }
}