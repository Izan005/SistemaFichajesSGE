package com.example.sistemafichajessge.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "task",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creador"],
            onDelete = NO_ACTION
        ),
        ForeignKey(
            entity = Department::class,
            parentColumns = ["id"],
            childColumns = ["departDestino"],
            onDelete = NO_ACTION
        )
    ]
)
data class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val timeStamp: Date,
    val creador: Int,
    val departDestino: Int,
    val isAll: Boolean = false,
    val estado: String = "hacer"
)