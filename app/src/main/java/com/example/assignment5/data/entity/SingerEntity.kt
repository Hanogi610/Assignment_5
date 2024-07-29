package com.example.assignment5.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "singer")
data class SingerEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val name : String
)