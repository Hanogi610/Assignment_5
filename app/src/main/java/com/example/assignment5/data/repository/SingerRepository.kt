package com.example.assignment5.data.repository

import com.example.assignment5.data.entity.SingerWithSongs
import kotlinx.coroutines.flow.Flow

interface SingerRepository {
    fun getSingers(): Flow<List<SingerWithSongs>>
    suspend fun getSingerById(id: Long): SingerWithSongs?
    suspend fun getSingerByName(name: String): SingerWithSongs?
    suspend fun insertSinger(singer: SingerWithSongs)
    suspend fun deleteSinger(id: Long)
}