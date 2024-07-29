package com.example.assignment5.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.assignment5.data.entity.PlaylistEntity
import com.example.assignment5.data.entity.PlaylistSongCrossRef
import com.example.assignment5.data.entity.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Transaction
    @Query("SELECT * FROM playlist")
    fun getPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistWithSongs?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongIntoPlaylist(playlistSongCrossRef: PlaylistSongCrossRef)

    @Transaction
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val playlistSongCrossRef = PlaylistSongCrossRef(playlistId, songId)
        insertSongIntoPlaylist(playlistSongCrossRef)
    }

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
}