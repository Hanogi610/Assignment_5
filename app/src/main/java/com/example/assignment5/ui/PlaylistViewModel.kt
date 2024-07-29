package com.example.assignment5.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment5.core.di.Dispatcher
import com.example.assignment5.core.di.MyDispatchers
import com.example.assignment5.data.entity.PlaylistEntity
import com.example.assignment5.data.entity.toPlaylist
import com.example.assignment5.data.model.Playlist
import com.example.assignment5.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    @Dispatcher(MyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _playlists = MutableStateFlow(emptyList<Playlist>())
    val playlists get() = _playlists

    init {
        viewModelScope.launch(ioDispatcher) {
            playlistRepository.getAllPlaylists().collect {
                    _playlists.value = it.map { playlistWithSongs ->
                        playlistWithSongs.toPlaylist()
                    }
                }
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity){
        viewModelScope.launch(ioDispatcher) {
            playlistRepository.deletePlaylist(playlist)
        }
    }

    fun addNewPlaylist(playlist: PlaylistEntity){
        viewModelScope.launch(ioDispatcher) {
            playlistRepository.insertPlaylist(playlist)
        }
    }
}