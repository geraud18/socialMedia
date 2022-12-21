package com.iris.socialmedia.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iris.socialmedia.model.ContactModel
import com.iris.socialmedia.model.EtatModel
import com.iris.socialmedia.repository.ContactRepository.Singleton.contactNotification
import com.iris.socialmedia.repository.ContactRepository.Singleton.dataBaseReferenceContact
import com.iris.socialmedia.repository.ContactRepository.Singleton.etatInvitationSend
import com.iris.socialmedia.repository.ContactRepository.Singleton.numberNotification

class ContactRepository {

    object Singleton {
        val dataBaseReferenceContact = FirebaseDatabase.getInstance().getReference("contacts")
        var contactData = ContactModel("","","","","")
        val contactList = arrayListOf<ContactModel>()
        val contactNotification = arrayListOf<ContactModel>()
        var etatInvitationSend:Boolean = false
        var numberNotification:Int = 0
    }

    fun checkInvitationSend(user_id: String, guest_id : String,callback: () -> Unit) {
        dataBaseReferenceContact.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                etatInvitationSend = false
                for(ds in snapshot.children){
                    val invitation = ds.getValue(ContactModel::class.java)
                    if(invitation != null){
                        if(invitation.id != ""){
                            if(invitation.user_id == user_id && invitation.guest_id == guest_id){
                                etatInvitationSend = true
                            }
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun countNotification(user_id : String,callback: () -> Unit){
        var countN:Int = 0
        dataBaseReferenceContact.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactNotification.clear()
                for(ds in snapshot.children){
                    val notification = ds.getValue(ContactModel::class.java)
                    if(notification != null){
                        if(notification.id != ""){
                            if(notification.user_id == user_id && notification.decision == "en attentes"){
                                countN++
                                contactNotification.add(notification)
                            }
                        }
                    }
                }
                numberNotification = countN
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun saveContact(contactModel: ContactModel) = dataBaseReferenceContact.child(contactModel.id).setValue(contactModel)
}