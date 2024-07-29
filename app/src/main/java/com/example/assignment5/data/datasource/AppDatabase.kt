package com.example.assignment5.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.assignment5.data.dao.PlaylistDao
import com.example.assignment5.data.dao.SingerDao
import com.example.assignment5.data.dao.SongDao
import com.example.assignment5.data.entity.PlaylistEntity
import com.example.assignment5.data.entity.PlaylistSongCrossRef
import com.example.assignment5.data.entity.SingerEntity
import com.example.assignment5.data.entity.SongEntity
import com.example.assignment5.data.model.Playlist

@Database(entities = [SingerEntity::class, SongEntity::class, PlaylistEntity::class, PlaylistSongCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun singerDao(): SingerDao
    abstract fun playlistDao(): PlaylistDao
}