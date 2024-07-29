package com.example.assignment5.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assignment5.R
import com.example.assignment5.databinding.FragmentPlaylistBinding
import com.example.assignment5.databinding.FragmentSongListBinding

class SongListFragment : Fragment() {

    companion object {
        fun newInstance() = SongListFragment()
    }

    private val viewModel: SongListViewModel by viewModels()
    private val mainViewModel : MainViewModel by activityViewModels()
    private var _binding : FragmentSongListBinding ?= null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongListBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.closeButton.setOnClickListener{
            mainViewModel.setSelectedPlaylist(null)
            mainViewModel.setSelectedSinger(null)
            findNavController().navigateUp()
        }

        return view
    }
}