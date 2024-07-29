package com.example.assignment5.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment5.core.di.Dispatcher
import com.example.assignment5.core.di.MyDispatchers
import com.example.assignment5.data.entity.SingerWithSongs
import com.example.assignment5.data.entity.toSinger
import com.example.assignment5.data.model.Singer
import com.example.assignment5.data.repository.SingerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingerViewModel @Inject constructor(
    private val singerRepository: SingerRepository,
    @Dispatcher(MyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _allSingers = MutableStateFlow<List<Singer>>(emptyList())
    val allSingers: StateFlow<List<Singer>> get() = _allSingers

    init {
        viewModelScope.launch(ioDispatcher) {
            singerRepository.getSingers()
                .distinctUntilChanged()
                .collect {
                    _allSingers.value = it.map { singer -> singer.toSinger() }
                    Log.d(TAG, "allsingers: ${_allSingers.value}")
                }
        }
    }

    fun getSingerById(id: Long): Singer?{
        var singer: Singer? = null
        viewModelScope.launch(ioDispatcher) {
            singer = singerRepository.getSingerById(id)?.toSinger()
        }
        return singer
    }

    fun deleteSinger(id: Long){
        viewModelScope.launch(ioDispatcher) {
            singerRepository.deleteSinger(id)
        }
    }

    fun insertSinger(singer: SingerWithSongs){
        viewModelScope.launch(ioDispatcher) {
            try {
                singerRepository.insertSinger(singer)
            }catch (e: IllegalArgumentException){
                Log.d(TAG, "insertSinger: ${e.message}")
            }
        }
    }
}
