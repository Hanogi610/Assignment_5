package com.example.assignment5.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.assignment5.R
import com.example.assignment5.data.entity.SongEntity
import com.example.assignment5.data.model.Song
import com.example.assignment5.databinding.SongItemViewBinding
import com.example.assignment5.ui.transformation.DiskShapeTransformation

class SongRvAdapter(
    private var songEntities: List<SongEntity>,
    private val itemClickListener: (SongEntity) -> Unit,
    private val deleteActionListener: (SongEntity) -> Unit,
    private val addToQueueActionListener: (SongEntity) -> Unit,
    private val addToPlaylistActionListener: (SongEntity) -> Unit
) : RecyclerView.Adapter<SongRvAdapter.SongViewHolder>() {
    private var lastPosition = -1

    class SongViewHolder(val binding: SongItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding =
            SongItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (songEntities.isEmpty()) 0 else songEntities.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songEntities[position]
        holder.binding.singerName.text = song.name
        holder.binding.deleteButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.song_item_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete -> {
                        deleteActionListener(song)
                        true
                    }
                    R.id.action_add_to_queue -> {
                        addToQueueActionListener(song)
                        true
                    }
                    R.id.action_add_to_playlist -> {
                        addToPlaylistActionListener(song)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
        holder.binding.root.setOnClickListener {
            itemClickListener(song)
        }
        holder.binding.songImg.load(song.albumCover) {
            transformations(DiskShapeTransformation())
            crossfade(true)
            placeholder(R.drawable.ic_music_note_24)
            error(R.drawable.ic_music_note_24)
        }

//        val animation = if (holder.adapterPosition > lastPosition) {
//            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.translation_up)
//        } else {
//            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.translation_down)
//        }
//        holder.itemView.startAnimation(animation)
//        lastPosition = holder.adapterPosition
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSongs(songs: List<SongEntity>) {
        songEntities = songs
        notifyDataSetChanged()
    }
}