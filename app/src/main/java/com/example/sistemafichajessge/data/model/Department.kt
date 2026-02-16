package com.example.sistemafichajessge.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "department")
data class Department(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
