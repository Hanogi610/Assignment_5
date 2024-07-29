package com.example.assignment5.ui

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment5.R
import com.example.assignment5.data.entity.PlaylistEntity
import com.example.assignment5.data.entity.toSingerWithSongs
import com.example.assignment5.data.model.Playlist
import com.example.assignment5.data.model.Singer
import com.example.assignment5.databinding.DialogAddEditBinding
import com.example.assignment5.databinding.FragmentPlaylistBinding
import com.example.assignment5.ui.adapter.PlaylistRvAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    private val mainViewModel : MainViewModel by activityViewModels()
    private val viewModel: PlaylistViewModel by viewModels()
    private var _binding : FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PlaylistRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = PlaylistRvAdapter(emptyList(), {
            // onPlaylistClick
            mainViewModel.setSelectedPlaylist(it)
            findNavController().navigate(R.id.action_viewPagerFragment_to_songListFragment)
        }, {
            // onMoreClick
        })

        binding.addPlaylistView.setOnClickListener{
            //addPlaylistDialog()
        }
        binding.playlistList.adapter = adapter
        binding.playlistList.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            viewModel.playlists.collect {
                adapter.updatePlaylists(it)
            }
        }
        return view
    }

    private fun addPlaylistDialog() {
        val dialogBinding = DialogAddEditBinding.inflate(layoutInflater)
        dialogBinding.editTextSingerName.hint = "Singer name"
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)

        builder.setTitle("Add Singer")

        builder.setPositiveButton("OK") { dialog, _ ->
            val singerName = dialogBinding.editTextSingerName.text.toString().trim()
            if (singerName.isNotEmpty()) {
                val playlist = PlaylistEntity(name = singerName, playlistId = 0)
                lifecycleScope.launch {
                    //viewModel.insertSinger(singer.toSingerWithSongs())
                    viewModel.addNewPlaylist(playlist)
                }
            } else {
                Toast.makeText(requireContext(), "Invalid name", Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

}