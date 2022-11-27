package com.iris.socialmedia.pages

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.PagesAdapter


class HomeActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tabLayout = findViewById(R.id.tabLayoutNavigation)
        viewPager = findViewById(R.id.viewPageNavigation)

        viewPager.adapter = PagesAdapter(this)

        TabLayoutMediator(tabLayout,viewPager){ tab,index ->

            when(index){
                0 -> { tab.setIcon(R.drawable.ic_home) }
                1 -> { tab.setIcon(R.drawable.ic_search) }
                2 -> { tab.setIcon(R.drawable.chat) }
                3 -> { tab.setIcon(R.drawable.ic_contact) }
                4 -> { tab.setIcon(R.drawable.ic_account) }
                else -> { throw Resources.NotFoundException("Position not found") }

            }

        }.attach()
    }
}