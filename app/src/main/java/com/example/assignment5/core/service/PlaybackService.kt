package com.example.assignment5.core.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.assignment5.R
import com.example.assignment5.core.receiver.ServiceReceiver
import com.example.assignment5.data.model.Song
import com.example.assignment5.ui.MainActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow

class PlaybackService : Service() {

    companion object {
        const val CHANNEL_ID = "PlaybackServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = 0
        const val ACTION_PAUSE = 1
        const val ACTION_RESUME = 2
        const val ACTION_SKIP_TO_NEXT = 3
        const val ACTION_SKIP_TO_PREVIOUS = 4
        const val ACTION_STOP = 5
        const val ACTION_OPEN = 6
        const val TAG = "PlaybackService"
    }

    private val binder = LocalBinder()
    private lateinit var player: SimpleExoPlayer
    private var playlist: List<Song> = emptyList()
    private var currentIndex: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isForeground = false
    val currentIndexFlow = MutableStateFlow(0)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = SimpleExoPlayer.Builder(this).build()
        player.addListener(PlayerListener())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras
        val action = bundle?.getInt("action_music_service", ACTION_PLAY)
        val index = bundle?.getInt("currentIndex", -1)
        if (playlist.isEmpty()) {
            playlist = try {
                bundle?.getParcelableArrayList<Song>("playlist")!!.toList()
            } catch (e: Exception) {
                emptyList()
            }
        }
        if (index != null && index != -1) {
            currentIndex = index
        }

        if (action != null) {
            handleActionMusic(action)
        }

        if (!isForeground) {
            startForeground(NOTIFICATION_ID, createNotification())
            isForeground = true
        } else {
            updateNotification()
        }
        if (playlist.isEmpty()) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }

        return START_NOT_STICKY
    }

    fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_PLAY -> playSong()
            ACTION_RESUME -> resumeSong()
            ACTION_PAUSE -> pauseSong()
            ACTION_SKIP_TO_NEXT -> nextSong()
            ACTION_SKIP_TO_PREVIOUS -> previousSong()
            ACTION_STOP -> stopService()
            ACTION_OPEN -> openMainActivity()
        }
    }

    private fun playSong() {
        if (playlist.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(playlist[currentIndex].url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }

    private fun resumeSong() {
        if (!player.isPlaying) player.play()
        updateNotification()
        sendUpdateToActivity(ACTION_RESUME)
    }

    private fun pauseSong() {
        if (player.isPlaying) player.pause()
        updateNotification()
        sendUpdateToActivity(ACTION_PAUSE)
    }

    private fun nextSong() {
        if (currentIndex < playlist.size - 1) {
            currentIndex++
            playSong()
            sendUpdateToActivity(ACTION_SKIP_TO_NEXT)
        }
    }

    private fun previousSong() {
        if (currentIndex > 0) {
            currentIndex--
            playSong()
            sendUpdateToActivity(ACTION_SKIP_TO_PREVIOUS)
        }
    }

    private fun stopService() {
        player.stop()
        playlist = emptyList()
        currentIndex = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        sendUpdateToActivity(ACTION_STOP)
        stopSelf()
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("navigate_to", "SongPlayerFragment")
            putExtra("currentIndex", currentIndex)
            putExtra("isPlaying", player.isPlaying)
            putExtra("progress", player.currentPosition)
            putParcelableArrayListExtra("playlist", ArrayList(playlist))
        }
        startActivity(intent)
    }

    @SuppressLint("RemoteViewLayout")
    private fun createNotification(): Notification {
        val collapsedView = RemoteViews(packageName, R.layout.notification_collapsed)
        val expandedView = RemoteViews(packageName, R.layout.notification_expanded)

        val songTitle = playlist.getOrNull(currentIndex)?.title ?: "No Song Playing"
        collapsedView.setTextViewText(R.id.notification_title, songTitle)
        expandedView.setTextViewText(R.id.notification_title, songTitle)

        val songArtist = "Artist Name"
        expandedView.setTextViewText(R.id.notification_artist, songArtist)

        val playPauseResId = if (player.isPlaying) R.drawable.ic_pause_circle_24 else R.drawable.ic_play_circle_24
        collapsedView.setImageViewResource(R.id.notification_play_pause, playPauseResId)
        expandedView.setImageViewResource(R.id.notification_play_pause, playPauseResId)

        collapsedView.setOnClickPendingIntent(R.id.notification_prev, getPendingIntent(ACTION_SKIP_TO_PREVIOUS))
        collapsedView.setOnClickPendingIntent(R.id.notification_play_pause, getPendingIntent(if (player.isPlaying) ACTION_PAUSE else ACTION_RESUME))
        collapsedView.setOnClickPendingIntent(R.id.notification_next, getPendingIntent(ACTION_SKIP_TO_NEXT))
        collapsedView.setOnClickPendingIntent(R.id.notification_close, getPendingIntent(ACTION_STOP))

        expandedView.setOnClickPendingIntent(R.id.notification_prev, getPendingIntent(ACTION_SKIP_TO_PREVIOUS))
        expandedView.setOnClickPendingIntent(R.id.notification_play_pause, getPendingIntent(if (player.isPlaying) ACTION_PAUSE else ACTION_RESUME))
        expandedView.setOnClickPendingIntent(R.id.notification_next, getPendingIntent(ACTION_SKIP_TO_NEXT))
        expandedView.setOnClickPendingIntent(R.id.notification_close, getPendingIntent(ACTION_STOP))

        val songCoverUrl = playlist.getOrNull(currentIndex)?.albumCover
//        songCoverUrl?.let { url ->
//            loadImage(url, collapsedView, R.id.notification_cover)
//            loadImage(url, expandedView, R.id.notification_cover)
//        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note_24)
            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(getPendingIntent(ACTION_OPEN))
            .build()
    }

    private fun loadImage(url: String, remoteViews: RemoteViews, viewId: Int) {
        val imageLoader = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(url)
            .target { result ->
                (result as? BitmapDrawable)?.bitmap?.let { bitmap ->
                    remoteViews.setImageViewBitmap(viewId, bitmap)
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, createNotification())
                }
            }
            .build()
        imageLoader.enqueue(request)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Playback Service Channel", NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        handler.removeCallbacksAndMessages(null)
    }

    inner class LocalBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private inner class PlayerListener : Player.Listener {
        @Deprecated("Deprecated in Java")
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                SimpleExoPlayer.STATE_READY -> Log.d(TAG, "Player is ready")
                SimpleExoPlayer.STATE_ENDED -> {
                    if (currentIndex < playlist.size - 1) {
                        currentIndex++
                        playSong()
                    } else {
                        stopSelf()
                    }
                }
                SimpleExoPlayer.STATE_BUFFERING -> Log.d(TAG, "Buffering")
                SimpleExoPlayer.STATE_IDLE -> Log.d(TAG, "Player is idle")
            }
            updateNotification()
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getPendingIntent(action: Int): PendingIntent {
        val intent = Intent(this, ServiceReceiver::class.java).apply {
            putExtra("action_music", action)
        }

        return PendingIntent.getBroadcast(
            applicationContext,
            action,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun sendUpdateToActivity(action: Int) {
        val intent = Intent("send_data_to_activity").apply {
            putExtra("currentIndex", currentIndex)
            putExtra("isPlaying", player.isPlaying)
            putExtra("progress", player.currentPosition)
            putExtra("action", action)
            putParcelableArrayListExtra("playlist", ArrayList(playlist))
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun getProgress() = player.currentPosition
    fun getDuration() = player.duration
}

