package com.example.assignment5.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.assignment5.data.model.Playlist
import com.example.assignment5.data.model.Song

data class PlaylistWithSongs(
    @Embedded var playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "songId",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    var songs: List<SongEntity>
)