package com.example.sistemafichajessge.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user",
    foreignKeys = [
        ForeignKey(
            entity = Department::class,
            parentColumns = ["id"],
            childColumns = ["departmentId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("departmentId")])
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val pass: String,
    @ColumnInfo(name = "departmentId")
    val departmentId: Int,
    val job: String
    )