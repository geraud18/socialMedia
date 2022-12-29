package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.iris.socialmedia.R

class SearchUserChatFragment(
    private val context: HomeActivity
) : Fragment() {


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val viewFragmentSearchUserChat = inflater.inflate(R.layout.fragment_search_user_chat, container, false)

        //Button close Search
        val btnCloseSearchUserChat = viewFragmentSearchUserChat.findViewById<ImageView>(R.id.btn_close_search_user_chat)
        //close search
        btnCloseSearchUserChat.setOnClickListener {
            context.onBackPressed()
        }


        return viewFragmentSearchUserChat
    }

}