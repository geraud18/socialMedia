package com.iris.socialmedia.adapter

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.iris.socialmedia.R
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.ContactModel
import com.iris.socialmedia.model.MessageModel
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.pages.MessageFragment
import com.iris.socialmedia.repository.MessageRepository
import com.iris.socialmedia.repository.MessageRepository.Singleton.dataBaseReferenceChat
import com.iris.socialmedia.repository.MessageRepository.Singleton.lastMessage
import com.iris.socialmedia.repository.MessageRepository.Singleton.messageExist
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationDataUser
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user

class ChatAdapter(
    private val context: HomeActivity,
    private val contactList: ArrayList<ContactModel>,
    private val layoutId: Int) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    val helpers = Helpers()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactImage = view.findViewById<ImageView>(R.id.chat_contact_image_profile)
        val contactName: TextView? = view.findViewById(R.id.chat_contact_name)
        val chatLastMessage: TextView? = view.findViewById(R.id.chat_last_message)
        val chatBloc = view.findViewById<LinearLayout>(R.id.container_bloc_chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutId,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentContact = contactList[position]
        val repoPublication = PublicationRepository()
        var sendMessageId = currentContact.user_id + currentContact.guest_id

        val repoMessage = MessageRepository()
        repoMessage.checkMessageExist(sendMessageId) {
            if(messageExist){

                repoPublication.getDataUser(currentContact.guest_id){
                        if(publicationDataUser.name == "" && publicationDataUser.firstname == ""){
                            holder.contactName?.text = "${publicationDataUser.username}"
                        }
                        else{
                            holder.contactName?.text = "${publicationDataUser.name}  ${publicationDataUser.firstname}"
                        }
                        val profileImage = holder.contactImage
                        if(publicationDataUser.profile?.isNotEmpty() == true){
                            if (profileImage != null) {
                                Glide.with(context).load(Uri.parse(publicationDataUser.profile)).into(profileImage)
                                helpers.updateCircleImage(profileImage)
                            }
                        }

                        holder.contactName?.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("receive_id", currentContact.guest_id)

                            val messageFragment: Fragment = MessageFragment(context)
                            messageFragment.arguments = bundle

                            val fragmentManager: FragmentManager = context.supportFragmentManager
                            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.setCustomAnimations(R.anim.slide_right, R.anim.slide_left,R.anim.slide_right, R.anim.slide_left)
                            fragmentTransaction.replace(R.id.homme_activity, messageFragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }

                        var lastMessageList = ArrayList<MessageModel>()
                        dataBaseReferenceChat.child(id_current_user!! + currentContact.guest_id).child("messages").addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(ds in snapshot.children){
                                    val messageItem = ds.getValue(MessageModel::class.java)
                                    if(messageItem != null){
                                        if(messageItem.id != ""){
                                            lastMessageList.add(messageItem)
                                        }
                                    }
                                }
                                lastMessageList.sortBy { it.date }
                                var last = lastMessageList[lastMessageList.lastIndex]
                                holder.chatLastMessage?.text = last.message
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })

                        holder.chatBloc.visibility = View.VISIBLE
                    }
            }
            else{
               // contactList.removeAt(position)
                holder.itemView.visibility = View.GONE
                holder.itemView.layoutParams.width = 0
                holder.itemView.layoutParams.height = 0
                holder.chatBloc.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = contactList.size

}