package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.CommentAdapter
import com.iris.socialmedia.adapter.NotificationAdapter
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.ContactRepository.Singleton.contactNotification
import com.iris.socialmedia.repository.EtatRepository
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user

class NotificationFragment(
    private val context: HomeActivity
) : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val viewFragmentNotification = inflater.inflate(R.layout.fragment_notification, container, false)

        val listNotificationRecycleView = viewFragmentNotification?.findViewById<RecyclerView>(R.id.notification_list)

        val toolBarNotification = viewFragmentNotification?.findViewById<Toolbar>(R.id.top_bar_notification)
        toolBarNotification?.setNavigationIcon(R.drawable.expandarrow)
        toolBarNotification?.setNavigationOnClickListener {
            context.onBackPressed()
        }

        var handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
                val repoContact = ContactRepository()
                repoContact.countNotification(id_current_user!!) {
                    if(contactNotification.size > 0){
                        contactNotification.sortByDescending { it.date }
                        listNotificationRecycleView?.adapter = NotificationAdapter(context, contactNotification ,R.layout.item_notification)
                    }
                }
            }
        },500)

        return viewFragmentNotification
    }

}