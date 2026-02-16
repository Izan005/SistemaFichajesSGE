package com.example.sistemafichajessge.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "registry",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("userId")])
data class Registry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "userId")
    val userId: Int,
    val type: String,
    val dateRegistry: Long,
    )
