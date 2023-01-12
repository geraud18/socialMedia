package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.CommentAdapter
import com.iris.socialmedia.adapter.MessageAdapter
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.repository.*
import com.iris.socialmedia.repository.MessageRepository.Singleton.messageData
import com.iris.socialmedia.repository.MessageRepository.Singleton.messageList
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationDataUser
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user
import java.text.SimpleDateFormat
import java.util.*

class MessageFragment(
    private val context: HomeActivity
) : Fragment() {

    private var lisMessageRecycleView: RecyclerView? = null
    private lateinit var messageAdapter: MessageAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val helpers = Helpers()

        val viewFragmentMessage = inflater.inflate(R.layout.fragment_message, container, false)

        val receiveId = requireArguments().getString("receive_id")

        lisMessageRecycleView = viewFragmentMessage?.findViewById(R.id.message_list)
        val messageSendButton = viewFragmentMessage?.findViewById<ImageButton>(R.id.message_send_button)
        val messageSendText = viewFragmentMessage?.findViewById<EditText>(R.id.message_send_text)

        var sendMessageId = id_current_user!! + receiveId.toString()
        var ReceiveMessageId = receiveId.toString() + id_current_user!!

        val repoMessage = MessageRepository()
        val repoPublication = PublicationRepository()

        val receiveUserImage = viewFragmentMessage?.findViewById<ImageView>(R.id.message_receive_user_image)
        val receiveUserName = viewFragmentMessage?.findViewById<TextView>(R.id.message_receive_user_name)

        repoPublication.getDataUser(receiveId.toString()){
            if(publicationDataUser.name == "" && publicationDataUser.firstname == ""){
                receiveUserName?.text = "${publicationDataUser.username}"
            }
            else{
                receiveUserName?.text = "${publicationDataUser.name}  ${publicationDataUser.firstname}"
            }
            if(publicationDataUser.profile?.isNotEmpty() == true){
                if (receiveUserImage != null) {
                    Glide.with(context).load(Uri.parse(publicationDataUser.profile)).into(receiveUserImage)
                    helpers.updateCircleImage(receiveUserImage)
                }
            }
            val imageCheckOnline = viewFragmentMessage?.findViewById<ImageView>(R.id.message_receive_check_online)
            val textCheckOnline = viewFragmentMessage?.findViewById<TextView>(R.id.message_receive_check_online_label)
            if(publicationDataUser.connect == "true"){
                imageCheckOnline?.setColorFilter(context.resources.getColor(R.color.figma_color_success))
                imageCheckOnline?.visibility = View.VISIBLE
                textCheckOnline?.text = context.getString(R.string.login_check_user_connect_true)
                textCheckOnline?.visibility = View.VISIBLE
            }else{
                imageCheckOnline?.setColorFilter(context.resources.getColor(R.color.figma_color_account_profile2))
                imageCheckOnline?.visibility = View.VISIBLE
                textCheckOnline?.text = context.getString(R.string.login_check_user_connect_false)
                textCheckOnline?.visibility = View.VISIBLE
            }
        }

        var handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
                repoMessage.messageListUser(sendMessageId) {
                    if(messageList.size > 0){
                        messageList.sortBy { it.date }
                        messageAdapter = MessageAdapter(context, messageList)
                        lisMessageRecycleView?.adapter = messageAdapter
                        lisMessageRecycleView?.visibility = View.VISIBLE
                    }
                }
            }
        },300)

        val toolBarMessage = viewFragmentMessage?.findViewById<Toolbar>(R.id.top_bar_message)
        toolBarMessage?.setNavigationIcon(R.drawable.custumleftarrow)
        toolBarMessage?.setNavigationOnClickListener {
            context.onBackPressed()
        }

        messageSendButton?.setOnClickListener {
            if(messageSendText?.text.toString() == ""){
                Toast.makeText(context,getString(R.string.notifie_publication_comment), Toast.LENGTH_SHORT).show()
            }else{
                messageData.id = UUID.randomUUID().toString()
                messageData.send_id = id_current_user!!
                messageData.message = messageSendText?.text.toString()

                val time = Calendar.getInstance().time
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentTime = formatter.format(time)

                messageData.date = currentTime

                repoMessage.saveMessage(messageData,sendMessageId)
                repoMessage.saveMessage(messageData,ReceiveMessageId)

                repoMessage.messageListUser(sendMessageId){
                    if(messageList.size > 0){
                        messageList.sortBy { it.date }
                        messageAdapter = MessageAdapter(context, messageList)
                        lisMessageRecycleView?.adapter = messageAdapter
                        messageAdapter.notifyDataSetChanged()
                        messageSendText?.setText("")
                    }
                  //  messageAdapter = MessageAdapter(context, messageList)
                    lisMessageRecycleView?.adapter = messageAdapter
                    messageAdapter.notifyDataSetChanged()
                   // messageSendText?.setText("")
                }
            }
        }

        return viewFragmentMessage
    }
}