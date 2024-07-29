package com.example.assignment5.data.model

data class Singer(
    var id : Long,
    val name : String,
    val songs : List<Song>? = emptyList()
)