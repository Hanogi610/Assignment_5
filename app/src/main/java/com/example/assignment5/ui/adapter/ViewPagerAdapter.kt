package com.example.assignment5.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.assignment5.ui.PlaylistFragment
import com.example.assignment5.ui.SingerFragment
import com.example.assignment5.ui.SongViewFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int) = when (position) {
        0 -> PlaylistFragment()
        1 -> SingerFragment()
        2 -> SongViewFragment()
        else -> PlaylistFragment()
    }
}