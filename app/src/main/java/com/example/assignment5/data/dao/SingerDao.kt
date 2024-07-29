package com.example.assignment5.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.assignment5.data.entity.SingerEntity
import com.example.assignment5.data.entity.SingerWithSongs
import kotlinx.coroutines.flow.Flow


@Dao
interface SingerDao {
    @Transaction
    @Query("SELECT * FROM singer")
    fun getAllSingers(): Flow<List<SingerWithSongs>>

    @Transaction
    @Query("SELECT * FROM singer WHERE id = :id")
    suspend fun getSingerById(id: Long): SingerWithSongs

    @Transaction
    @Query("SELECT * FROM singer WHERE name = :name")
    suspend fun getSingerByName(name: String): SingerWithSongs

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSinger(singer: SingerEntity): Long

    @Query("DELETE FROM singer WHERE id = :id")
    suspend fun deleteSingerEntity(id: Long)

    @Query("DELETE FROM song WHERE singerId = :singerId")
    suspend fun deleteSongOfSinger(singerId : Long)

    @Transaction
    suspend fun deleteSinger(id: Long) {
        deleteSongOfSinger(id)
        deleteSingerEntity(id)
    }
}