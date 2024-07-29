package com.example.assignment5.data.impl

import com.example.assignment5.data.dao.PlaylistDao
import com.example.assignment5.data.entity.PlaylistEntity
import com.example.assignment5.data.entity.PlaylistSongCrossRef
import com.example.assignment5.data.entity.PlaylistWithSongs
import com.example.assignment5.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val dao: PlaylistDao
) : PlaylistRepository {
    override fun getAllPlaylists(): Flow<List<PlaylistWithSongs>> {
        return dao.getPlaylistsWithSongs()
    }

    override fun getSongsInPlaylist(playlistId: Long): Flow<List<PlaylistSongCrossRef>> = flow {
        val songs = dao.getPlaylistById(playlistId)?.songs?.map { song ->
            PlaylistSongCrossRef(playlistId, song.songId)
        } ?: emptyList()
        emit(songs)
    }

    override suspend fun insertPlaylist(playlist: PlaylistEntity): Long {
        return dao.insertPlaylist(playlist)
    }

    override suspend fun insertSongInPlaylist(playlistId: Long, songId: Long) {
        dao.addSongToPlaylist(playlistId, songId)
    }

    override suspend fun deletePlaylist(playlist: PlaylistEntity) {
        dao.deletePlaylist(playlist)
    }

    override suspend fun deleteSongInPlaylist(playlistId: Long, songId: Long) {
        dao.removeSongFromPlaylist(playlistId, songId)
    }
}