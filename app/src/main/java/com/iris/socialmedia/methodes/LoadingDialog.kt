package com.iris.socialmedia.methodes

import android.app.Activity
import android.app.AlertDialog
import com.iris.socialmedia.R

class LoadingDialog(val mActivity:Activity) {

    private lateinit var isDialog:AlertDialog
    fun startLoading(){
        val inflater = mActivity.layoutInflater
        var dialogView = inflater.inflate(R.layout.loading_item,null)
        var builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }
    fun isDismiss(){
        isDialog.dismiss()
    }
}