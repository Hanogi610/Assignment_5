package com.example.assignment5.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assignment5.R
import com.example.assignment5.databinding.FragmentViewPagerBinding
import com.example.assignment5.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ViewPagerFragment : Fragment() {

    companion object {
        fun newInstance() = ViewPagerFragment()
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ViewPagerAdapter
    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = ViewPagerAdapter(this)
        binding.mainViewPager.adapter = adapter

        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Playlist"
                1 -> tab.text = "Artist"
                2 -> tab.text = "Album"
            }
        }.attach()

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                mainViewModel.playbackState.collect { it ->
                    if (it.songQueue.isNotEmpty()) {
                        binding.currentSongView.visibility = View.VISIBLE
                        val currentSong = it.songQueue.getOrNull(it.currentSongIndex)
                        binding.songName.text = currentSong?.name ?: "Unknown Song"
                    } else {
                        binding.currentSongView.visibility = View.GONE
                    }
                    val controllerIcon = if (it.isPlaying) {
                        R.drawable.ic_pause_circle_24
                    } else {
                        R.drawable.ic_play_circle_24
                    }
                    binding.controllerButton.setImageResource(controllerIcon)
                }
            }
        }

        binding.controllerButton.setOnClickListener {
            mainViewModel.setPlaying(!mainViewModel.playbackState.value.isPlaying)
        }

        binding.currentSongView.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_songPlayerFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
