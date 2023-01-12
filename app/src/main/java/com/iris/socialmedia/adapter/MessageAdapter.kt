package com.iris.socialmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iris.socialmedia.R
import com.iris.socialmedia.model.MessageModel
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user

class MessageAdapter(
    val context: HomeActivity,
    private val listMessage: ArrayList<MessageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SEND = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val view = LayoutInflater.from(context).inflate(R.layout.item_message_receive,parent,false)
            return ReceiveViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.item_message_send,parent,false)
            return SendViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = listMessage[position]

        if(holder.javaClass == SendViewHolder::class.java){
            val viewHolder = holder as SendViewHolder
            holder.sendMessage.text = currentMessage.message
        }else{
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = listMessage[position]

        if(id_current_user!!.equals(currentMessage.send_id)){
            return ITEM_SEND
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int = listMessage.size

    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sendMessage = itemView.findViewById<TextView>(R.id.message_text_send)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.message_text_receive)
    }
}