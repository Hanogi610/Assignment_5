package com.example.assignment5.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: Long,
    val title: String,
    val albumCover: String? = null,
    val url: String
) : Parcelable
