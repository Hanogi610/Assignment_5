package com.example.assignment5.data.entity

import com.example.assignment5.data.model.Playlist
import com.example.assignment5.data.model.Singer
import com.example.assignment5.data.model.Song

fun Singer.toEntity(): SingerEntity {
    return SingerEntity(
        id = this.id,
        name = this.name,
    )
}

fun Song.toEntity(singerId: Long): SongEntity {
    return SongEntity(
        songId = this.id,
        name = this.title,
        albumCover = this.albumCover!!,
        singerId = singerId,
        url = this.url
    )
}

fun SingerEntity.toNormal(songs: List<SongEntity>): Singer {
    return Singer(
        id = this.id,
        name = this.name,
        songs = songs.map { songEntity -> songEntity.toNormal() })
}

fun SongEntity.toNormal(): Song {
    return Song(
        id = this.songId,
        title = this.name,
        albumCover = this.albumCover,
        url = this.url
    )
}

fun SingerWithSongs.toSinger(): Singer {
    return Singer(id = this.singer.id,
        name = this.singer.name,
        songs = this.songs.map { songEntity -> songEntity.toNormal() })
}

fun Singer.toSingerWithSongs(): SingerWithSongs {
    return SingerWithSongs(
        singer = this.toEntity(),
        songs = this.songs!!.map { it.toEntity(this.id) })
}

fun PlaylistWithSongs.toPlaylist(): Playlist {
    return Playlist(id = this.playlist.playlistId,
        name = this.playlist.name,
        songs = this.songs.map { songEntity -> songEntity.toNormal() })
}