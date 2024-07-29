package com.example.assignment5.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.assignment5.R
import com.example.assignment5.core.receiver.ServiceReceiver
import com.example.assignment5.data.model.Song
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class SongPlayerService : Service() {

    companion object {
        const val CHANNEL_ID = "PlaybackServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = 0
        const val ACTION_PAUSE = 1
        const val ACTION_SKIP_TO_NEXT = 2
        const val ACTION_SKIP_TO_PREVIOUS = 3
        const val ACTION_STOP = 4
        const val TAG = "SongPlayerService"
    }

    private lateinit var player: SimpleExoPlayer
    private var playlist: List<Song> = emptyList()
    private var currentIndex: Int = -1
    private val handler = Handler(Looper.getMainLooper())
    private var isForeground = false
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var playbackStateBuilder: PlaybackStateCompat.Builder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = SimpleExoPlayer.Builder(this).build()
        player.addListener(PlayerListener())

        // Initialize MediaSession
        mediaSession = MediaSessionCompat(this, "PlaybackService")
        mediaSession.isActive = true

        playbackStateBuilder = PlaybackStateCompat.Builder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras
        val action = bundle?.getInt("action_music_service", ACTION_PLAY)
        val index = bundle?.getInt("currentIndex", -1)
        if (playlist.isEmpty()) {
            playlist = bundle?.getParcelableArrayList<Song>("playlist")!!.toList()
        }
        if (index != -1) {
            currentIndex = index!!
        }

        handleActionMusic(action!!)

        if (!isForeground) {
            startForeground(NOTIFICATION_ID, createNotification())
            isForeground = true
        } else {
            updateNotification()
        }

        return START_NOT_STICKY
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_PLAY -> {
                playSong()
            }

            ACTION_PAUSE -> pauseSong()
            ACTION_SKIP_TO_NEXT -> nextSong()
            ACTION_SKIP_TO_PREVIOUS -> previousSong()
            ACTION_STOP -> {
                player.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun playSong() {
        if (playlist.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(playlist[currentIndex].url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()

            // Build MediaMetadata
            val mediaMetadataBuilder = MediaMetadataCompat.Builder()
            mediaMetadataBuilder.putString(
                MediaMetadataCompat.METADATA_KEY_TITLE, playlist[currentIndex].title
            )
            mediaMetadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Unknown")
            mediaMetadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, player.duration)
            Log.d("SongPlayerService", "playSong: ${player.duration}")

            // Set MediaMetadata to MediaSession
            mediaSession.setMetadata(mediaMetadataBuilder.build())

            // Build and set PlaybackState
            val state =
                if (player.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            val playbackState =
                playbackStateBuilder.setState(state, player.currentPosition, 1.0f).setActions(
                    PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SEEK_TO
                ).build()
            mediaSession.setPlaybackState(playbackState)
        }
    }

    private fun pauseSong() {
        Log.d(TAG, "pauseSong: ")
        player.pause()
        updateNotification()
    }

    private fun nextSong() {
        Log.d(TAG, "before nextSong: $currentIndex/${playlist.size}")
        if (currentIndex < playlist.size - 1) {
            currentIndex++
            playSong()
            updateNotification()
        }
    }

    private fun previousSong() {
        Log.d(TAG, "previousSong: $currentIndex/${playlist.size}")
        if (currentIndex > 0) {
            currentIndex--
            playSong()
            updateNotification()
        }
    }

    private fun getPendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(this, ServiceReceiver::class.java)
        intent.putExtra("action_music", action)

        return PendingIntent.getBroadcast(
            context.applicationContext,
            action,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotification(): Notification {
        val songTitle =
            if (playlist.isNotEmpty()) playlist[currentIndex].title else "No Song Playing"


        return NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(songTitle)
            .setContentText("Playing Music").setSmallIcon(R.drawable.ic_music_note_24).addAction(
                R.drawable.ic_skip_previous_24, "Previous", getPendingIntent(
                    this, ACTION_SKIP_TO_PREVIOUS
                )
            ).addAction(
                if (player.isPlaying) R.drawable.ic_pause_circle_24 else R.drawable.ic_play_circle_24,
                if (player.isPlaying) "Pause" else "Play",
                if (player.isPlaying) getPendingIntent(this, ACTION_PAUSE)
                else getPendingIntent(
                    this, ACTION_PLAY
                )
            ).addAction(
                R.drawable.ic_skip_next_24, "Next", getPendingIntent(this, ACTION_SKIP_TO_NEXT)
            ).addAction(R.drawable.ic_close_24, "Close", getPendingIntent(this, ACTION_STOP))
            .setStyle(
                MediaStyle().setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            ).build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Playback Service Channel", NotificationManager.IMPORTANCE_LOW
            )
            serviceChannel.setSound(null, null)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        handler.removeCallbacksAndMessages(null)
        mediaSession.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private inner class PlayerListener : Player.Listener {
        @Deprecated("Deprecated in Java")
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    Log.d(TAG, "Player is ready")
                    updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
                }

                Player.STATE_ENDED -> {
                    if (currentIndex < playlist.size - 1) {
                        currentIndex++
                        playSong()
                    } else {
                        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
                        stopSelf()
                    }
                }

                Player.STATE_BUFFERING -> {
                    Log.d(TAG, "Buffering")
                    updatePlaybackState(PlaybackStateCompat.STATE_BUFFERING)
                }

                Player.STATE_IDLE -> {
                    Log.d(TAG, "Player is idle")
                    updatePlaybackState(PlaybackStateCompat.STATE_NONE)
                }
            }
            updateNotification()
        }

        private fun updatePlaybackState(state: Int) {
            val playbackState =
                playbackStateBuilder.setState(state, player.currentPosition, 1.0f).build()
            mediaSession.setPlaybackState(playbackState)
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}
