package com.iris.socialmedia.pages

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.MainActivity
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.PagesAdapter
import com.iris.socialmedia.auth.LoginActivity
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.ContactRepository.Singleton.numberNotification
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user
import java.lang.String
import java.util.*
import kotlin.Boolean
import kotlin.Int


class HomeActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    var menu_add : Boolean = true

    var textCartItemCount: TextView? = null
    val helpers = Helpers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_home)

        val toolBarEditAccount = findViewById<Toolbar>(R.id.topBar)

        setSupportActionBar(toolBarEditAccount)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        tabLayout = findViewById(R.id.tabLayoutNavigation)
        viewPager = findViewById(R.id.viewPageNavigation)

        viewPager.adapter = PagesAdapter(this)

        TabLayoutMediator(tabLayout,viewPager){ tab,index ->

            when(index){
                0 -> { tab.setIcon(R.drawable.homepage) }
                1 -> { tab.setIcon(R.drawable.custumsearch) }
                2 -> { tab.setIcon(R.drawable.chat) }
                3 -> { tab.setIcon(R.drawable.custumadduser) }
                4 -> { tab.setIcon(R.drawable.custumuser) }
                else -> { throw Resources.NotFoundException(getString(R.string.error_swipe_app)) }

            }

        }.attach()


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                if (position == 0) {
                    menu_add = true
                    invalidateOptionsMenu()
                }
                else if (position == 1) {
                    menu_add = true
                    invalidateOptionsMenu()
                }
                else if (position == 2) {
                    menu_add = true
                    invalidateOptionsMenu()
                }
                else if (position == 3) {
                    menu_add = true
                    invalidateOptionsMenu()
                }
                else if (position == 4) {
                    menu_add = false
                    invalidateOptionsMenu()
                }
                else{
                    menu_add = true
                    invalidateOptionsMenu()
                }
                super.onPageSelected(position)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if(menu_add){
            menuInflater.inflate(R.menu.action_bar_menu, menu)
        }else{
            menuInflater.inflate(R.menu.action_bar_menu_hide_item, menu)
        }

        val menuItem = menu!!.findItem(R.id.menu_notification)
        val actionView: View = menuItem.actionView
        textCartItemCount = actionView.findViewById(R.id.cart_badge)

        val repoContact = ContactRepository()

        repoContact.countNotification(id_current_user!!){
            setupBadge(numberNotification)
        }

        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupBadge(value:Int) {
        if (textCartItemCount != null) {
            if (value === 0) {
                if (textCartItemCount?.visibility !== View.GONE) {
                    textCartItemCount?.visibility = View.GONE
                }
            } else {
                textCartItemCount?.text = value.toString()
                if (textCartItemCount?.visibility !== View.VISIBLE) {
                    textCartItemCount?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.menu_notification -> {
                getNotificationPage()
                true
            }
            R.id.menu_add_publication -> {
                getPublicationPage()
                true
            }
            R.id.menu_logout -> {
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getNotificationPage() {
        val notificationFragment: Fragment = NotificationFragment(this)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
        fragmentTransaction.replace(R.id.homme_activity, notificationFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun logoutUser() {
        val repoUser = UserRepository()
        repoUser.logoutDataUser{
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
            val loginActivity = Intent(this, MainActivity::class.java)
            startActivity(loginActivity)
            Toast.makeText(this,getString(R.string.app_logout),Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getPublicationPage() {
        val publicationFragment: Fragment = PublicationFragment(this)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
        fragmentTransaction.replace(R.id.homme_activity, publicationFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}