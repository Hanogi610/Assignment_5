package com.example.assignment5.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SongPlayerViewModel : ViewModel() {
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying get() = _isPlaying

    private val _progress = MutableStateFlow(0)
    val progress get() = _progress

    fun togglePlayPause(){
        _isPlaying.value = !_isPlaying.value
    }

    fun setProgress(progress: Int){
        _progress.value = progress
    }
}