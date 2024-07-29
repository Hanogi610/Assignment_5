package com.example.assignment5.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment5.R
import com.example.assignment5.data.entity.toSingerWithSongs
import com.example.assignment5.data.model.Singer
import com.example.assignment5.databinding.DialogAddEditBinding
import com.example.assignment5.databinding.FragmentSingerBinding
import com.example.assignment5.ui.adapter.SingerRvAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SingerFragment : Fragment() {

    companion object {
        fun newInstance() = SingerFragment()
    }

    private val viewModel: SingerViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSingerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SingerRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingerBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = "Singers"

        adapter = SingerRvAdapter(emptyList(), {
            // itemClickListener
            mainViewModel.setSelectedSinger(it)
            findNavController().navigate(R.id.action_viewPagerFragment_to_songListFragment)
        }, {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteSinger(it.id)
            }
        })

        binding.singerRv.adapter = adapter
        binding.singerRv.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allSingers.collect { singers ->
                adapter.updateSingers(singers)
            }
        }
        binding.add.setOnClickListener {
            addSingerDialog()
        }

        return view
    }

    private fun addSingerDialog() {
        val dialogBinding = DialogAddEditBinding.inflate(layoutInflater)
        dialogBinding.editTextSingerName.hint = "Singer name"
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)

        builder.setTitle("Add Singer")

        builder.setPositiveButton("OK") { dialog, _ ->
            val singerName = dialogBinding.editTextSingerName.text.toString().trim()
            if (singerName.isNotEmpty()) {
                val singer = Singer(id = 0, name = singerName)
                lifecycleScope.launch {
                    viewModel.insertSinger(singer.toSingerWithSongs())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}