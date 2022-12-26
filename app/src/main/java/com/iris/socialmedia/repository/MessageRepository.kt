package com.iris.socialmedia.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iris.socialmedia.model.MessageModel
import com.iris.socialmedia.repository.MessageRepository.Singleton.dataBaseReferenceChat
import com.iris.socialmedia.repository.MessageRepository.Singleton.messageList

class MessageRepository {

    object Singleton {
        val dataBaseReferenceChat = FirebaseDatabase.getInstance().getReference("chats")
        var messageData = MessageModel("","","","")
        val messageList = ArrayList<MessageModel>()
    }


    fun messageListUser(parentId: String,callback: () -> Unit){
        dataBaseReferenceChat.child(parentId).child("messages").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(ds in snapshot.children){
                    val messageItem = ds.getValue(MessageModel::class.java)
                    if(messageItem != null){
                        if(messageItem.id != ""){
                            messageList.add(messageItem)
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun saveMessage(messageModel: MessageModel, parentId: String) = dataBaseReferenceChat.child(parentId)
        .child("messages").child(messageModel.id).setValue(messageModel)


}