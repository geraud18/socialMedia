package com.iris.socialmedia.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iris.socialmedia.R


class ChatFragment(
    private val context: HomeActivity
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewFragmentChat = inflater.inflate(R.layout.fragment_chat, container, false)

        return viewFragmentChat
    }

}