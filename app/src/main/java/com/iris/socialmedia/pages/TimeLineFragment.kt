package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.CommentAdapter
import com.iris.socialmedia.adapter.ImagePublicationAdapter
import com.iris.socialmedia.adapter.TimeLineAdapter
import com.iris.socialmedia.repository.EtatRepository
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationList
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth

class TimeLineFragment(
    private val context: HomeActivity
) : Fragment() {

    private var listTimeLineRecycleView: RecyclerView? = null
    private lateinit var timeLineAdapter: TimeLineAdapter

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val viewFragmentTimeLine = inflater.inflate(R.layout.fragment_time_line, container, false)

        viewFragmentTimeLine?.findViewById<ProgressBar>(R.id.time_line_progressbar_load_data)?.visibility = View.VISIBLE

        listTimeLineRecycleView = viewFragmentTimeLine?.findViewById(R.id.time_line_list)

        var handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
                val repoPublication = PublicationRepository()
                repoPublication.initDataPublication(null) {
                    if(publicationList.size > 0){
                        timeLineAdapter = TimeLineAdapter(context, publicationList.sortedBy { it.date },R.layout.item_time_line)
                        listTimeLineRecycleView?.adapter = timeLineAdapter
                        // viewFragmentTimeLine?.findViewById<ProgressBar>(R.id.time_line_progressbar_load_data)?.visibility = View.GONE
                        viewFragmentTimeLine?.findViewById<ProgressBar>(R.id.time_line_progressbar_load_data)?.visibility = View.GONE
                        listTimeLineRecycleView?.visibility = View.VISIBLE

                    }else{
                        viewFragmentTimeLine?.findViewById<ProgressBar>(R.id.time_line_progressbar_load_data)?.visibility = View.GONE
                        viewFragmentTimeLine?.findViewById<TextView>(R.id.time_line_data)?.visibility = View.VISIBLE
                    }
                }
            }
        },1000)

        val swipeRefreshLayout = viewFragmentTimeLine?.findViewById<SwipeRefreshLayout>(R.id.refreshLayoutTimeLine)
        swipeRefreshLayout?.setOnRefreshListener{
            var handler = Handler()
            handler.postDelayed(object :Runnable{
                override fun run(){
                    val repoPublication = PublicationRepository()
                    repoPublication.initDataPublication(null) {
                        if(publicationList.size > 0){
                            timeLineAdapter =  TimeLineAdapter(context, publicationList.sortedBy { it.date },R.layout.item_time_line)
                            listTimeLineRecycleView?.adapter = timeLineAdapter
                            timeLineAdapter.notifyDataSetChanged()
                          //  viewFragmentTimeLine?.findViewById<ProgressBar>(R.id.time_line_progressbar_load_data)?.visibility = View.GONE
                        }else{
                            viewFragmentTimeLine?.findViewById<TextView>(R.id.time_line_data)?.visibility = View.VISIBLE
                        }
                    }
                }
            },500)
            swipeRefreshLayout.isRefreshing = false
        }

        return viewFragmentTimeLine
    }

   /* fun RecyclerView.runWhenReady(action: () -> Unit) {
        val globalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                action()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }*/

}