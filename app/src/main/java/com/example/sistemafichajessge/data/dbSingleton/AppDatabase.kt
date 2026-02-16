package com.example.sistemafichajessge.data.dbSingleton

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sistemafichajessge.data.dao.DepartmentDao
import com.example.sistemafichajessge.data.dao.RegistryDao
import com.example.sistemafichajessge.data.dao.UserDao
import com.example.sistemafichajessge.data.model.Department
import com.example.sistemafichajessge.data.model.Registry
import com.example.sistemafichajessge.data.model.User

@Database(
    entities = [
        User::class,
        Registry::class,
        Department::class       ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun departmentDao(): DepartmentDao
    abstract fun registryDao(): RegistryDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sistemaFichajes"
                )
                    // Leemos la base de datos con información ya creada
                    .createFromAsset("sistemaFichajes.db")
                    // Eliminamos la antigua base de datos y la volvemos a leer
                    // cada vez que ejecutemos la app (solo recomendable en versiones
                    // de pruebas).
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}