package com.iris.socialmedia.pages

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.ContactAdapter
import com.iris.socialmedia.adapter.TimeLineAdapter
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.ContactRepository.Singleton.contactList
import com.iris.socialmedia.repository.ContactRepository.Singleton.searchContactList
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user
import androidx.appcompat.widget.SearchView


class ContactFragment(
    private val context: HomeActivity
) : Fragment() {

    private var contactSearch: SearchView? = null
    private var listContactRecycleView: RecyclerView? = null
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val viewFragmentContact = inflater.inflate(R.layout.fragment_contact, container, false)
        listContactRecycleView = viewFragmentContact?.findViewById(R.id.contact_list)
        contactSearch = viewFragmentContact?.findViewById(R.id.contact_search)

        var handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
                val repoContact = ContactRepository()
                repoContact.contactListUser(id_current_user!!) {
                    if(contactList.size > 0){
                        contactList.sortByDescending { it.date }
                        contactAdapter = ContactAdapter(context, contactList,R.layout.item_contact)
                        listContactRecycleView?.adapter = contactAdapter
                        listContactRecycleView?.visibility = View.VISIBLE
                    }
                }
            }
        },400)

        contactSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                filterContact(p0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                filterContact(p0)
                return true
            }
        })

        return viewFragmentContact
    }

    private fun filterContact(p0: String?) {
        val repoContact = ContactRepository()
        repoContact.searchContactListUser(id_current_user!!,p0.toString()){
            if(searchContactList.size > 0){
                searchContactList.sortByDescending { it.date }
                contactAdapter =  ContactAdapter(context, searchContactList,R.layout.item_contact)
                listContactRecycleView?.adapter = contactAdapter
                contactAdapter.notifyDataSetChanged()
            }
        }
    }

}