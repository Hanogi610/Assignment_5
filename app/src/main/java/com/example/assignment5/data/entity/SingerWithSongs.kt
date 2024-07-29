package com.example.assignment5.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SingerWithSongs(
    @Embedded val singer: SingerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "singerId"
    )
    val songs: List<SongEntity>
)