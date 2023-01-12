package com.iris.socialmedia.pages

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.TimeLineAdapter
import com.iris.socialmedia.model.PublicationModel
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.UserRepository

class PopupOption(
    private val adapter: TimeLineAdapter,
    private val idUser: String
)  : Dialog(adapter.context){

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_remove_friend)
        setupRemove()
        setupCloseButton()
    }

    private fun setupRemove() {
        findViewById<ImageView>(R.id.timeline_option_confirm_delete).setOnClickListener{
            val repoContact = ContactRepository()
            repoContact.removecontactListUser(UserRepository.Singleton.id_current_user!!,idUser){
                dismiss()
            }
        }
    }

    private fun setupCloseButton() {
        findViewById<ImageView>(R.id.timeline_option_cancel_delete).setOnClickListener{
            dismiss()
        }
    }

}