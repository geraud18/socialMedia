package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.TimeLineAdapter
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationList
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth


class TimeLineUserFragment(
    private val context: HomeActivity
) : Fragment() {

    private var listTimeLineUserRecycleView: RecyclerView? = null
    private lateinit var timeLineAdapter: TimeLineAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val viewFragmentTimeLineUser = inflater.inflate(R.layout.fragment_time_line_user, container, false)

        listTimeLineUserRecycleView = viewFragmentTimeLineUser?.findViewById(R.id.time_line_user_list)
        val toolBarNotification = viewFragmentTimeLineUser?.findViewById<Toolbar>(R.id.top_bar_time_line_user)
        toolBarNotification?.setNavigationIcon(R.drawable.expandarrow)
        toolBarNotification?.setNavigationOnClickListener {
            context.onBackPressed()
        }

        val userId = requireArguments().getString("user_id")

        var handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
                val repoPublication = PublicationRepository()
                repoPublication.initDataPublication(userId) {
                    if(publicationList.size > 0){
                        timeLineAdapter = TimeLineAdapter(context, publicationList.sortedBy { it.date },R.layout.item_time_line)
                        listTimeLineUserRecycleView?.adapter = timeLineAdapter
                        listTimeLineUserRecycleView?.visibility = View.VISIBLE

                    }else{
                        viewFragmentTimeLineUser?.findViewById<TextView>(R.id.time_line_data)?.visibility = View.VISIBLE
                    }
                }
            }
        },1000)

        return viewFragmentTimeLineUser
    }

}