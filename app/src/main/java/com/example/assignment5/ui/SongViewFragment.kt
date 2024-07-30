package com.example.assignment5.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assignment5.R
import com.example.assignment5.core.service.PlaybackService
import com.example.assignment5.data.entity.SongEntity
import com.example.assignment5.data.entity.toNormal
import com.example.assignment5.data.model.Singer
import com.example.assignment5.databinding.DialogAddEditBinding
import com.example.assignment5.databinding.FragmentSongViewBinding
import com.example.assignment5.ui.adapter.SingerSpinnerAdapter
import com.example.assignment5.ui.adapter.SongRvAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongViewFragment : Fragment() {

    companion object {
        fun newInstance() = SongViewFragment()
        const val TAG = "SongViewFragment"
    }

    private var _binding: FragmentSongViewBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: SongViewViewModel by viewModels()
    private lateinit var songs: List<SongEntity>
    private lateinit var singers: List<Singer>
    private lateinit var adapter: SongRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.songs.collect {
                songs = it
            }
        }

        lifecycleScope.launch {
            viewModel.singers.collect {
                singers = it
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongViewBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = SongRvAdapter(emptyList(), {
            mainViewModel.setSongQueue(songs)
            mainViewModel.setCurrentSongIndex(songs.indexOf(it))
            mainViewModel.setPlaying(true)
            findNavController().navigate(R.id.action_viewPagerFragment_to_songPlayerFragment)
        }, {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteSong(it.songId)
                mainViewModel.removeSongFromQueue(it)
            }
        },{
            mainViewModel.addSongToQueue(it)
            mainViewModel.setPlaying(true)
        },{
            // Add to playlist
        })


        binding.songRv.adapter = adapter
        binding.songRv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.songs.collect { songs ->
                Log.d(TAG, "onCreateView: $songs")
                adapter.updateSongs(songs)
            }
        }

        binding.addSongFab.setOnClickListener {
            alertDialogAddSong()
        }

        return view
    }

    private fun alertDialogAddSong() {
        val dialogBinding = DialogAddEditBinding.inflate(layoutInflater)
        dialogBinding.editTextSingerName.hint = "Song Name"
        dialogBinding.textInputLayoutAlbumCover.visibility = View.VISIBLE
        dialogBinding.spinnerSingers.visibility = View.VISIBLE
        dialogBinding.textInputLayoutUrl.visibility = View.VISIBLE

        // Set up the spinner with singer names
        val singerAdapter = SingerSpinnerAdapter(requireContext(), singers)
        dialogBinding.spinnerSingers.adapter = singerAdapter

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)

        builder.setTitle("Add Song")
        builder.setPositiveButton("OK") { dialog, _ ->
            val songName = dialogBinding.editTextSingerName.text.toString().trim()
            val albumCover = dialogBinding.editTextAlbumCover.text.toString().trim()
            val selectedSinger = dialogBinding.spinnerSingers.selectedItem as Singer
            val url = dialogBinding.editTextUrl.text.toString().trim()

            if (songName.isNotEmpty() && albumCover.isNotEmpty() && selectedSinger.id != 0L) {
                val song = SongEntity(
                    songId = 0,
                    name = songName,
                    singerId = selectedSinger.id,
                    albumCover = albumCover,
                    url = url
                )
                lifecycleScope.launch {
                    viewModel.insertSong(song)
                }
            } else {
                Toast.makeText(
                    requireContext(), "Some fields are invalid. Please try again", Toast.LENGTH_LONG
                ).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}