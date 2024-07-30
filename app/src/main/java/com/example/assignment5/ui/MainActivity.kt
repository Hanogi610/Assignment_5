package com.example.assignment5.ui

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.assignment5.R
import com.example.assignment5.core.service.PlaybackService
import com.example.assignment5.core.util.AppStart
import com.example.assignment5.core.util.checkAppStart
import com.example.assignment5.data.entity.toEntity
import com.example.assignment5.data.entity.toNormal
import com.example.assignment5.data.model.Song
import com.example.assignment5.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var previousPlaybackState: PlaybackState? = null
    private val viewModel: MainViewModel by viewModels()
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val songQueue = intent?.getParcelableArrayListExtra<Song>("playlist")
            val isPlaying = intent?.getBooleanExtra("isPlaying", false)
            val currentSongIndex = intent?.getIntExtra("currentIndex", 0)
            val action = intent?.getIntExtra("action", PlaybackService.ACTION_PLAY)
            when (action) {
                PlaybackService.ACTION_PAUSE -> {
                    viewModel.setPlaying(false)
                }
                PlaybackService.ACTION_RESUME -> {
                    viewModel.setPlaying(true)
                }
                PlaybackService.ACTION_SKIP_TO_PREVIOUS -> {
                    viewModel.setCurrentSongIndex(currentSongIndex!!)
                }
                PlaybackService.ACTION_SKIP_TO_NEXT -> {
                    viewModel.setCurrentSongIndex(currentSongIndex!!)
                }
                PlaybackService.ACTION_STOP -> {
                    viewModel.setPlaying(false)
                    viewModel.setSongQueue(emptyList())
                    viewModel.setCurrentSongIndex(0)
                }
                PlaybackService.ACTION_OPEN -> {
                    viewModel.setPlaying(isPlaying ?: true)
                    viewModel.setSongQueue(songQueue?.map { it.toEntity(0) } ?: emptyList())
                    viewModel.setCurrentSongIndex(currentSongIndex ?: 0)
                }
            }
        }
    }
    private var playbackService: PlaybackService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PlaybackService.LocalBinder
            playbackService = binder.getService()
            isBound = true
            viewModel.setPlaybackService(playbackService)
            handleServiceConnection()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            viewModel.setPlaybackService(null)
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter("send_data_to_activity"))

        initAppStart(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment?.findNavController()

        lifecycleScope.launch {
            viewModel.playbackState.collect { currentState ->
                val playlistChanged = previousPlaybackState?.songQueue != currentState.songQueue
                val songIndexChanged = previousPlaybackState?.currentSongIndex != currentState.currentSongIndex
                val isPlayingChanged = previousPlaybackState?.isPlaying != currentState.isPlaying

                if (playlistChanged || songIndexChanged || isPlayingChanged) {
                    if (currentState.isPlaying && currentState.songQueue.isNotEmpty()) {
                        val intent = Intent(this@MainActivity, PlaybackService::class.java)
                        val bundle = Bundle().apply {
                            putParcelableArrayList(
                                "playlist", ArrayList(currentState.songQueue.map { song -> song.toNormal() })
                            )
                        }
                        intent.putExtras(bundle)
                        intent.putExtra("currentIndex", currentState.currentSongIndex)
                        intent.putExtra("isPlaying", true)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent)
                        } else {
                            startService(intent)
                        }
                    }
                    if (!currentState.isPlaying) {
                        playbackService?.handleActionMusic(PlaybackService.ACTION_PAUSE)
                    }
                    if (currentState.songQueue.isEmpty()) {
                        playbackService?.handleActionMusic(PlaybackService.ACTION_STOP)
                    }
                    // Update the previous state
                    previousPlaybackState = currentState
                }
            }
        }
    }

    private fun handleServiceConnection() {
        if (intent?.getStringExtra("navigate_to") == "SongPlayerFragment") {
            val isPlaying = intent?.getBooleanExtra("isPlaying", true)
            val songQueue = intent?.getParcelableArrayListExtra<Song>("playlist")
            val currentSongIndex = intent?.getIntExtra("currentIndex", 0)

            Log.d(TAG, "Received Intent Data: currentIndex=$currentSongIndex, isPlaying=$isPlaying, playlistSize=${songQueue?.size}")

            viewModel.setPlaying(isPlaying ?: true)
            viewModel.setSongQueue(songQueue?.map { it.toEntity(0) } ?: emptyList())
            viewModel.setCurrentSongIndex(currentSongIndex ?: 0)
            Log.d(TAG, "on opening main activity: ${viewModel.playbackState.value}")

            // Navigate to SongPlayerFragment if needed
            findNavController(R.id.nav_host_fragment).navigate(R.id.songPlayerFragment)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (isBound) {
            handleServiceConnection()
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, PlaybackService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    private fun initAppStart(context: Context) {
        when (checkAppStart(context)) {
            AppStart.FIRST_TIME -> {
                lifecycleScope.launch {
                    viewModel.insertMockData()
                }
            }
            AppStart.NORMAL -> {}
            AppStart.FIRST_TIME_VERSION -> {}
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}