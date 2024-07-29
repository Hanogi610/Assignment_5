package com.example.assignment5.data.impl

import com.example.assignment5.data.dao.SingerDao
import com.example.assignment5.data.dao.SongDao
import com.example.assignment5.data.entity.SingerWithSongs
import com.example.assignment5.data.repository.SingerRepository
import kotlinx.coroutines.flow.Flow

class SingerRepositoryImpl(
    private val dao: SingerDao, private val songDao: SongDao
) : SingerRepository {

    override fun getSingers(): Flow<List<SingerWithSongs>> {
        return dao.getAllSingers()
    }

    override suspend fun getSingerById(id: Long): SingerWithSongs {
        return dao.getSingerById(id)
    }

    override suspend fun getSingerByName(name: String): SingerWithSongs {
        return dao.getSingerByName(name)
    }

    override suspend fun insertSinger(singer: SingerWithSongs) {
        if (singer.singer.name.isEmpty()) {
            throw IllegalArgumentException("Name cannot be empty")
        } else {
            val singerId = dao.insertSinger(singer.singer)
            singer.songs.forEach {
                it.singerId = singerId.toLong()
                songDao.insertSong(it)
            }
        }
    }

    override suspend fun deleteSinger(id: Long) {
        dao.deleteSinger(id)
    }
}