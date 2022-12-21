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

class PopupOption(
    private val adapter: TimeLineAdapter,
    private val currentPulication: PublicationModel
)  : Dialog(adapter.context){

    override fun onCreate(savedInstanceState: Bundle?) {
       // requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_option_item)

       // val frameLayout =  findViewById<FrameLayout>(R.id.plant_detail_popup_name)
/*
        BottomSheetBehavior.from(frameLayout).apply {
            peekHeight = 200
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }*/

       // setupCloseButton()
    }

    private fun setupCloseButton() {
            dismiss()
    }

}