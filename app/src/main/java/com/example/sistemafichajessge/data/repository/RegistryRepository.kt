package com.example.sistemafichajessge.data.repository

import com.example.sistemafichajessge.data.dao.RegistryDao
import com.example.sistemafichajessge.data.model.Registry
import kotlinx.coroutines.flow.Flow

class RegistryRepository(private val dao: RegistryDao){
     suspend fun insert(registry: Registry) {
         dao.insert(registry)
    }

     suspend fun update(registry: Registry) {
        dao.update(registry)
    }

     suspend fun delete(registry: Registry) {
        dao.delete(registry)
    }

     fun getRegistry(id: Int): Flow<Registry> {
        return dao.getRegistry(id)
    }

     fun getAllRegistries(): Flow<List<Registry>> {
        return dao.getAllRegistries()
    }

    fun getRegistriesByUserId(userId: Int): Flow<List<Registry>> {
        return dao.getRegistriesByUserId(userId)
    }
}