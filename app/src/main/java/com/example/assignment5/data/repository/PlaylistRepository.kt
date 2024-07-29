package com.example.assignment5.data.repository

import com.example.assignment5.data.entity.PlaylistEntity
import com.example.assignment5.data.entity.PlaylistSongCrossRef
import com.example.assignment5.data.entity.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<PlaylistWithSongs>>

    fun getSongsInPlaylist(playlistId: Long): Flow<List<PlaylistSongCrossRef>>

    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    suspend fun insertSongInPlaylist(playlistId: Long, songId: Long)

    suspend fun deletePlaylist(playlist: PlaylistEntity)

    suspend fun deleteSongInPlaylist(playlistId: Long, songId: Long)
}