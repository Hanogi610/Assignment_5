package com.example.assignment5.data.impl

import com.example.assignment5.data.dao.SongDao
import com.example.assignment5.data.entity.SongEntity
import com.example.assignment5.data.repository.SongRepository
import kotlinx.coroutines.flow.Flow

class SongRepositoryImpl(private val dao: SongDao) : SongRepository {
    override fun getSongs(): Flow<List<SongEntity>> {
        return dao.getAllSongs()
    }

    override fun getSongsBySingerId(singerId: Long): Flow<List<SongEntity>> {
        return dao.getSongsBySingerId(singerId)
    }

    override suspend fun getSongById(id: Long): SongEntity {
        return dao.getSongById(id)
    }

    override suspend fun getSongByName(name: String): SongEntity {
        return dao.getSongByName(name)
    }

    override suspend fun insertSong(song: SongEntity) {
        if (song.name.isEmpty()) {
            throw IllegalArgumentException("Name cannot be empty")
        } else {
            dao.insertSong(song)
        }
    }

    override suspend fun deleteSong(id: Long) {
        dao.deleteSongFromPlaylists(id)
        dao.deleteSong(id)
    }

    override suspend fun deleteSongsBySingerId(singerId: Long) {
        dao.deleteSongsBySingerId(singerId)
    }
}