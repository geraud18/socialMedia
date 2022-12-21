package com.iris.socialmedia.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iris.socialmedia.pages.*

class PagesAdapter(private val fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
       return when(position){
           0 -> { TimeLineFragment(fragmentActivity as HomeActivity) }
           1 -> { SearchFragment(fragmentActivity as HomeActivity) }
           2 -> { ChatFragment(fragmentActivity as HomeActivity) }
           3 -> { ContactFragment(fragmentActivity as HomeActivity) }
           4 -> { AccountFragment(fragmentActivity as HomeActivity) }
           else -> { throw Resources.NotFoundException("Position not found") }
       }
    }
}