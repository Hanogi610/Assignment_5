package com.example.assignment5.data.repository

import com.example.assignment5.data.entity.SongEntity
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getSongs(): Flow<List<SongEntity>>
    fun getSongsBySingerId(singerId: Long): Flow<List<SongEntity>>
    suspend fun getSongById(id: Long): SongEntity
    suspend fun getSongByName(name: String): SongEntity
    suspend fun insertSong(song: SongEntity)
    suspend fun deleteSong(id: Long)
    suspend fun deleteSongsBySingerId(singerId: Long)
}