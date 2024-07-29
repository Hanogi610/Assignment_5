package com.example.assignment5.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment5.data.entity.SongEntity
import com.example.assignment5.databinding.SongQueueBottomSheetItemBinding

class BottomSheetRvAdapter(
    private var songQueue: List<SongEntity>,
    private val onItemClickListener: (SongEntity) -> Unit,
    private val onItemRemoveClickListener: (SongEntity) -> Unit
) : RecyclerView.Adapter<BottomSheetRvAdapter.ItemViewHolder>() {
    class ItemViewHolder (val binding: SongQueueBottomSheetItemBinding) : RecyclerView.ViewHolder(binding.root)

    private var currentlyPlayingPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = SongQueueBottomSheetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (songQueue.isEmpty()) 1 else songQueue.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val song = songQueue.getOrNull(position)
        Log.d("Bottomsheet Rv", "onBindViewHolder: $song")
        if (song != null) {
            holder.binding.singerName.text = song.name
            holder.binding.root.setOnClickListener {
                onItemClickListener(song)
            }
            holder.binding.deleteButton.setOnClickListener {
                onItemRemoveClickListener(song)
            }

            // Highlight the currently playing item
            if (position == currentlyPlayingPosition) {
                holder.binding.root.setBackgroundColor(Color.YELLOW) // Change this to the color you want
            } else {
                holder.binding.root.setBackgroundColor(Color.TRANSPARENT) // Change this to your default color
            }
        } else {
            holder.binding.singerName.text = "No songs in queue"
        }
    }

    fun updateCurrentlyPlayingPosition(position: Int) {
        val oldPosition = currentlyPlayingPosition
        currentlyPlayingPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(currentlyPlayingPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSongs(songQueue: List<SongEntity>) {
        this.songQueue = songQueue
        notifyDataSetChanged()
    }
}