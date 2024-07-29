package com.example.assignment5.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assignment5.data.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM song WHERE songId = :id")
    suspend fun getSongById(id: Long): SongEntity

    @Query("SELECT * FROM song WHERE name = :name")
    suspend fun getSongByName(name: String): SongEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Query("DELETE FROM song WHERE songId = :id")
    suspend fun deleteSong(id: Long)

    @Query("DELETE FROM playlist_song_cross_ref WHERE songId = :id")
    suspend fun deleteSongFromPlaylists(id: Long)

    @Query("SELECT * FROM song WHERE singerId = :singerId")
    fun getSongsBySingerId(singerId: Long): Flow<List<SongEntity>>

    @Query("DELETE FROM song WHERE singerId = :singerId")
    suspend fun deleteSongsBySingerId(singerId: Long)
}