package com.example.assignment5.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment5.data.model.Playlist
import com.example.assignment5.databinding.SongItemViewBinding

class PlaylistRvAdapter(
    private var playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit,
    private val onMoreClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistRvAdapter.ItemViewHolder>(
) {
    class ItemViewHolder(val binding: SongItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            SongItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (playlists.isEmpty()) 0 else playlists.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
        holder.binding.deleteButton.setOnClickListener{
            onMoreClick(playlist)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylists(playlists: List<Playlist>) {
        this.playlists = playlists
        notifyDataSetChanged()
    }
}