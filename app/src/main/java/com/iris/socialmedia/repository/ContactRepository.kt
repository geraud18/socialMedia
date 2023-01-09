package com.iris.socialmedia.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iris.socialmedia.R
import com.iris.socialmedia.model.ContactModel
import com.iris.socialmedia.model.EtatModel
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.repository.ContactRepository.Singleton.contactList
import com.iris.socialmedia.repository.ContactRepository.Singleton.contactNotification
import com.iris.socialmedia.repository.ContactRepository.Singleton.dataBaseReferenceContact
import com.iris.socialmedia.repository.ContactRepository.Singleton.etatInvitationSend
import com.iris.socialmedia.repository.ContactRepository.Singleton.numberNotification
import com.iris.socialmedia.repository.ContactRepository.Singleton.searchContactList

class ContactRepository {

    object Singleton {
        val dataBaseReferenceContact = FirebaseDatabase.getInstance().getReference("contacts")
        var contactData = ContactModel("","","","","")
        val contactList = ArrayList<ContactModel>()
        val searchContactList = ArrayList<ContactModel>()
        val contactNotification = ArrayList<ContactModel>()
        var etatInvitationSend:String = ""
        var numberNotification:Int = 0
    }


    fun contactListUser(user_id : String,callback: () -> Unit){
        dataBaseReferenceContact.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                for(ds in snapshot.children){
                    val contactItem = ds.getValue(ContactModel::class.java)
                    if(contactItem != null){
                        if(contactItem.id != ""){
                            if(contactItem.user_id == user_id && contactItem.decision == "accpeter"){
                                contactList.add(contactItem)
                            }
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun searchContactListUser(user_id : String,searchValue: String,callback: () -> Unit){
        dataBaseReferenceContact.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                searchContactList.clear()
                for(ds in snapshot.children){
                    val searchContactItem = ds.getValue(ContactModel::class.java)
                    if(searchContactItem != null){
                        if(searchContactItem.id != ""){
                            if(searchContactItem.user_id == user_id && searchContactItem.decision == "accpeter"){
                                if(searchContactItem.user_name_guest.contains(searchValue, ignoreCase = true)){
                                    searchContactList.add(searchContactItem)
                                }
                            }
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun checkInvitationSend(context: HomeActivity, user_id: String, guest_id : String,callback: () -> Unit) {
        dataBaseReferenceContact.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                etatInvitationSend = ""
                for(ds in snapshot.children){
                    val invitation = ds.getValue(ContactModel::class.java)
                    if(invitation != null){
                        if(invitation.id != ""){
                            if(invitation.user_id == user_id && invitation.guest_id == guest_id && invitation.decision == "en attentes"){
                                etatInvitationSend = context.getString(R.string.account_user_invitation)
                            }else if(invitation.user_id == user_id && invitation.guest_id == guest_id && invitation.decision == "accpeter"){
                                etatInvitationSend = context.getString(R.string.account_num_friend_label_single)
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

    fun acceptContact(user_id: String, guest_id : String,userName: String,callback: () -> Unit){
        dataBaseReferenceContact.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val invitation = ds.getValue(ContactModel::class.java)
                    if(invitation != null){
                        if(invitation.id != ""){
                            if(invitation.user_id == user_id && invitation.guest_id == guest_id && invitation.decision == "en attentes"){
                                val map = mutableMapOf<String, String>()
                                map["decision"] = "accpeter"
                                map["user_name_guest"] = userName
                                ds.ref.updateChildren(map as Map<String, Any>)
                            }
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun saveContact(contactModel: ContactModel) = dataBaseReferenceContact.child(contactModel.id).setValue(contactModel)

    fun deleteContact(contactModel: ContactModel) = dataBaseReferenceContact.child(contactModel.id).removeValue()
}