package com.example.assignment5.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "song", foreignKeys = [ForeignKey(
        entity = SingerEntity::class,
        parentColumns = ["id"],
        childColumns = ["singerId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("singerId")]
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true) val songId: Long,
    val name: String,
    val albumCover: String,
    val url : String,
    var singerId: Long
)