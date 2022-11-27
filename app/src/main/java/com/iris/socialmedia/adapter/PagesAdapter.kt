package com.iris.socialmedia.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iris.socialmedia.pages.*

class PagesAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
       return when(position){
           0 -> { TimeLineFragment() }
           1 -> { SearchFragment() }
           2 -> { ChatFragment() }
           3 -> { ContactFragment() }
           4 -> { AccountFragment() }
           else -> { throw Resources.NotFoundException("Position not found") }
       }
    }
}