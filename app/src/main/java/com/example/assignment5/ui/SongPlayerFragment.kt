package com.example.assignment5.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.assignment5.R
import com.example.assignment5.core.service.PlaybackService
import com.example.assignment5.data.entity.SongEntity
import com.example.assignment5.data.model.Singer
import com.example.assignment5.databinding.FragmentSongPlayerBinding
import com.example.assignment5.databinding.SongQueueBottomSheetBinding
import com.example.assignment5.ui.adapter.BottomSheetRvAdapter
import com.example.assignment5.ui.transformation.DiskShapeTransformation
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SongPlayerFragment : Fragment() {

    companion object {
        fun newInstance() = SongPlayerFragment()
        const val TAG = "SongPlayerFragment"
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSongPlayerBinding? = null
    private val binding get() = _binding!!
    private val adapter : BottomSheetRvAdapter by lazy {
        BottomSheetRvAdapter(mainViewModel.playbackState.value.songQueue, { song ->
            mainViewModel.setCurrentSongIndex(mainViewModel.playbackState.value.songQueue.indexOf(song))
            mainViewModel.setPlaying(true)
        }, { song ->
            mainViewModel.removeSongFromQueue(song)
        })
    }
    private val bottomSheetDialog by lazy { BottomSheetDialog(requireContext()) }
    private val bottomSheetView by lazy { SongQueueBottomSheetBinding.inflate(layoutInflater) }
    private val rotationAnimation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_infinitely) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongPlayerBinding.inflate(inflater, container, false)
        val view = binding.root

        lifecycleScope.launch {
            mainViewModel.playbackState.collect{
                updateUI(it.songQueue)
                updateCurrentSong(it.currentSongIndex)
                binding.playPauseButton.setImageResource(
                    if (it.isPlaying) R.drawable.ic_pause_circle_24 else R.drawable.ic_play_circle_24
                )
            }
        }

        lifecycleScope.launch {
            mainViewModel.playbackService.collect { playbackService ->
                // Handle playbackService if needed
                // Example: Update UI based on playbackService changes
            }
        }

        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.playPauseButton.setOnClickListener {
            mainViewModel.setPlaying(!mainViewModel.playbackState.value.isPlaying)
        }

        binding.nextButton.setOnClickListener {
            mainViewModel.setPlaying(true)
        }

        binding.previousButton.setOnClickListener {
            mainViewModel.setPlaying(true)
        }

        binding.upNextTextView.setOnClickListener {
            bottomSheetDialog.show()
        }

        showBottomSheet()
        return view
    }

    private fun updateUI(songQueue: List<SongEntity>) {
        adapter.updateSongs(songQueue)
        adapter.updateCurrentlyPlayingPosition(mainViewModel.playbackState.value.currentSongIndex)
        //binding.seekBar.max = playbackService?.getDuration()?.toInt() ?: 0
    }

    private fun updateCurrentSong(index: Int) {
        adapter.updateCurrentlyPlayingPosition(index)
        val song = mainViewModel.playbackState.value.songQueue.getOrNull(index)
        binding.coverImageView.load(song?.albumCover) {
            transformations(DiskShapeTransformation())
            placeholder(R.drawable.ic_music_note_24)
            error(R.drawable.ic_music_note_24)
        }
        binding.songTitleTextView.text = song?.name
        // binding.artistNameTextView.text = singer?.name
        if (mainViewModel.playbackState.value.isPlaying) {
            binding.coverImageView.startAnimation(rotationAnimation)
        } else {
            binding.coverImageView.clearAnimation()
        }
    }

    private fun showBottomSheet() {
        bottomSheetDialog.setContentView(bottomSheetView.root)

//        adapter = BottomSheetRvAdapter(mainViewModel.songQueue.value, { song ->
//            mainViewModel.setSongQueue(mainViewModel.songQueue.value)
//            mainViewModel.setCurrentSongIndex(mainViewModel.songQueue.value.indexOf(song))
//            mainViewModel.setPlaying(true)
//        }, { song ->
//            mainViewModel.removeSongFromQueue(song)
//        })

        bottomSheetView.songQueueRecyclerView.adapter = adapter
        bottomSheetView.songQueueRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Cancel any ongoing jobs or clean up resources if needed
    }
}
