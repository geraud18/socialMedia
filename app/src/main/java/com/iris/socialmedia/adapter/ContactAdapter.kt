package com.iris.socialmedia.adapter

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iris.socialmedia.R
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.ContactModel
import com.iris.socialmedia.pages.EditAccountFragment
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.pages.MessageFragment
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationDataUser

class ContactAdapter(
    private val context: HomeActivity,
    private val contactList: ArrayList<ContactModel>,
    private val layoutId: Int) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    val helpers = Helpers()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactImage = view.findViewById<ImageView>(R.id.contact_image_profile)
        val contactName: TextView? = view.findViewById(R.id.contact_name)
        val contactChat = view.findViewById<ImageView>(R.id.contact_chat)
        val contactDelete = view.findViewById<ImageView>(R.id.contact_delete)
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
        val repoContact = ContactRepository()

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
        }

        holder.contactChat.setOnClickListener {

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

        holder.contactDelete.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            dialog.setContentView(R.layout.popup_option_delete_contact)
           // dialog.setCancelable(false)

            val imageProfileUser = dialog.findViewById<ImageView>(R.id.contact_image_delete)
            val contactConfirmDelete = dialog.findViewById<ImageView>(R.id.contact_confirm_delete)
            val contactCancelDelete = dialog.findViewById<ImageView>(R.id.contact_cancel_delete)

            repoPublication.getDataUser(currentContact.guest_id){
                if(publicationDataUser.profile?.isNotEmpty() == true){
                    if (imageProfileUser != null) {
                        Glide.with(context).load(Uri.parse(publicationDataUser.profile)).into(imageProfileUser)
                        helpers.updateCircleImage(imageProfileUser)
                    }
                }
            }

            contactConfirmDelete?.setOnClickListener {
                repoContact.deleteContact(currentContact)
                dialog.dismiss()
                contactList.removeAt(position)
                this.notifyItemRemoved(position)
                Toast.makeText(context,context.getString(R.string.contact_delete_confirm), Toast.LENGTH_SHORT).show()
            }

            contactCancelDelete?.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

    }

    override fun getItemCount(): Int = contactList.size

}