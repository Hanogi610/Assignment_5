package com.example.assignment5.core.di

import android.content.Context
import androidx.room.Room
import com.example.assignment5.data.datasource.AppDatabase
import com.example.assignment5.data.impl.PlaylistRepositoryImpl
import com.example.assignment5.data.impl.SingerRepositoryImpl
import com.example.assignment5.data.impl.SongRepositoryImpl
import com.example.assignment5.data.repository.PlaylistRepository
import com.example.assignment5.data.repository.SingerRepository
import com.example.assignment5.data.repository.SongRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Dispatcher(MyDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(MyDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext, AppDatabase::class.java, "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSingerRepository(database: AppDatabase): SingerRepository {
        return SingerRepositoryImpl(database.singerDao(), database.songDao())
    }

    @Provides
    @Singleton
    fun provideSongRepository(database: AppDatabase): SongRepository {
        return SongRepositoryImpl(database.songDao())
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(database: AppDatabase): PlaylistRepository {
        return PlaylistRepositoryImpl(database.playlistDao())
    }

}