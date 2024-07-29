package com.example.assignment5.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment5.core.di.Dispatcher
import com.example.assignment5.core.di.MyDispatchers
import com.example.assignment5.data.entity.SongEntity
import com.example.assignment5.data.entity.toSinger
import com.example.assignment5.data.model.Singer
import com.example.assignment5.data.repository.SingerRepository
import com.example.assignment5.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val singerRepository: SingerRepository,
    @Dispatcher(MyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _songs = MutableStateFlow<List<SongEntity>>(emptyList())
    val songs get() = _songs

    private val _singers = MutableStateFlow<List<Singer>>(emptyList())
    val singers get() = _singers

    init {
        viewModelScope.launch(ioDispatcher) {
            songRepository.getSongs()
                .distinctUntilChanged()
                .collect {
                    _songs.value = it
                }
        }
        viewModelScope.launch(ioDispatcher) {
            singerRepository.getSingers()
                .distinctUntilChanged()
                .collect {
                    _singers.value = it.map { singer -> singer.toSinger()}
                }
        }
    }

    fun insertSong(song: SongEntity){
        viewModelScope.launch(ioDispatcher) {
            songRepository.insertSong(song)
        }
    }

    fun deleteSong(id: Long){
        viewModelScope.launch(ioDispatcher) {
            songRepository.deleteSong(id)
        }
    }
}