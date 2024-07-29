package com.example.assignment5.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment5.core.service.PlaybackService
import com.example.assignment5.core.util.MockData
import com.example.assignment5.data.entity.SongEntity
import com.example.assignment5.data.model.Playlist
import com.example.assignment5.data.model.Singer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mockData: MockData
) : ViewModel() {
    private val _playbackService = MutableStateFlow<PlaybackService?>(null)
    val playbackService: StateFlow<PlaybackService?> get() = _playbackService

    private val _selectedSinger = MutableStateFlow<Singer?>(null)
    val selectedSinger: StateFlow<Singer?> get() = _selectedSinger

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> get() = _selectedPlaylist

    private val _songQueue = MutableStateFlow<List<SongEntity>>(emptyList())
    val songQueue: StateFlow<List<SongEntity>> get() = _songQueue

    private val _currentSongIndex = MutableStateFlow(0)
    val currentSongIndex: StateFlow<Int> get() = _currentSongIndex

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> get() = _progress

    fun setPlaybackService(service: PlaybackService?) {
        _playbackService.value = service
        if (service != null) {
            viewModelScope.launch {
                service.currentIndexFlow.collect {
                    _currentSongIndex.value = it
                }
            }
        }
    }

    fun setSelectedSinger(singer: Singer?) {
        _selectedSinger.value = singer
    }

    fun setSelectedPlaylist(playlist: Playlist?) {
        _selectedPlaylist.value = playlist
    }

    fun setSongQueue(songs: List<SongEntity>) {
        _songQueue.value = songs
    }

    fun addSongToQueue(song: SongEntity) {
        _songQueue.value = _songQueue.value.toMutableList().apply {
            if (!contains(song)) add(song)
        }
    }

    fun removeSongFromQueue(song: SongEntity) {
        _songQueue.value = _songQueue.value.toMutableList().apply {
            remove(song)
        }
    }

    fun setCurrentSongIndex(index: Int) {
        _currentSongIndex.value = index
    }

    fun setPlaying(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun setProgress(progress: Int) {
        _progress.value = progress
    }

    suspend fun insertMockData() {
        mockData()
    }
}

