package com.example.assignment5.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment5.R
import com.example.assignment5.data.model.Singer
import com.example.assignment5.databinding.ItemViewBinding
import javax.inject.Inject

class SingerRvAdapter @Inject constructor(
    private var singerEntities: List<Singer>,
    private val itemClickListener: (Singer) -> Unit,
    private val deleteButtonClickListener: (Singer) -> Unit
) : RecyclerView.Adapter<SingerRvAdapter.ItemView>() {
    private var lastPosition = -1

    class ItemView(val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemView(binding)
    }

    override fun getItemCount(): Int {
        return singerEntities.size
    }

    override fun onBindViewHolder(holder: ItemView, position: Int) {
        val singer = singerEntities[position]
        holder.binding.singerName.text = singer.name
        holder.binding.root.setOnClickListener {
            itemClickListener(singer)
        }
        holder.binding.deleteButton.setOnClickListener {
            deleteButtonClickListener(singer)
        }

//        holder.itemView.clearAnimation()
//
//        val animation: Animation = if (position > lastPosition) {
//            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.translation_up)
//        } else {
//            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.translation_down)
//        }
//
//        holder.itemView.startAnimation(animation)
//        lastPosition = holder.adapterPosition
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSingers(singers: List<Singer>) {
        singerEntities = singers
        notifyDataSetChanged()
    }
}
