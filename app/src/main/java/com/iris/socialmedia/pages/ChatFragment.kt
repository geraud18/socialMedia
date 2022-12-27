package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.ChatAdapter
import com.iris.socialmedia.adapter.ContactAdapter
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.ContactRepository.Singleton.contactList
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user


class ChatFragment(
    private val context: HomeActivity
) : Fragment() {

    private var listChatRecycleView: RecyclerView? = null
    private lateinit var chatAdapter: ChatAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragmentChat = inflater.inflate(R.layout.fragment_chat, container, false)

        listChatRecycleView = viewFragmentChat?.findViewById(R.id.chat_list)

        var handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
                val repoContact = ContactRepository()
                repoContact.contactListUser(id_current_user!!) {
                    if(contactList.size > 0){
                        contactList.sortByDescending { it.date }
                        chatAdapter = ChatAdapter(context, contactList,R.layout.item_chat)
                        listChatRecycleView?.adapter = chatAdapter
                        listChatRecycleView?.visibility = View.VISIBLE
                    }
                }
            }
        },400)

        return viewFragmentChat
    }

}