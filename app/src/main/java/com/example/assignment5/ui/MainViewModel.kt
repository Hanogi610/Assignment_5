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

data class PlaybackState(
    val currentSongIndex: Int = 0,
    val songQueue: List<SongEntity> = emptyList(),
    val isPlaying: Boolean = false,
    val progress: Int = 0
)

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

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> get() = _playbackState

    fun setPlaybackService(service: PlaybackService?) {
        _playbackService.value = service
        if(service!=null){
            viewModelScope.launch {
                service.currentIndexFlow.collect{
                    _playbackState.value = _playbackState.value.copy(currentSongIndex = it)
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
        _playbackState.value = _playbackState.value.copy(songQueue = songs.toMutableList())
    }

    fun addSongToQueue(song: SongEntity) {
        if(_playbackState.value.songQueue.isEmpty()) {
            _playbackState.value = _playbackState.value.copy(songQueue = listOf(song))
        } else {
            if(!_playbackState.value.songQueue.contains(song)) {
                _playbackState.value = _playbackState.value.copy(songQueue = _playbackState.value.songQueue + song)
            }
        }
    }

    fun removeSongFromQueue(song: SongEntity) {
        if(_playbackState.value.songQueue.isNotEmpty()) {
            _playbackState.value = _playbackState.value.copy(songQueue = _playbackState.value.songQueue - song)
        }
    }

    fun setCurrentSongIndex(index: Int) {
        _playbackState.value = _playbackState.value.copy(currentSongIndex = index)
    }

    fun setPlaying(isPlaying: Boolean) {
        _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
    }

    fun setProgress(progress: Int) {
        _playbackState.value = _playbackState.value.copy(progress = progress)
    }

    suspend fun insertMockData() {
        mockData()
    }
}
