package com.example.assignment5.data.model

data class Playlist(
    val id : Long,
    val name: String,
    val songs: List<Song>
)