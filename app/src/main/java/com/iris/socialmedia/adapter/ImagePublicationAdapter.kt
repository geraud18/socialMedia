package com.iris.socialmedia.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.iris.socialmedia.pages.HomeActivity
import java.util.ArrayList


class ImagePublicationAdapter(
    private val context: HomeActivity,
    private val images: ArrayList<String?>,
) : BaseAdapter() {

    override fun getCount(): Int = images.size

    override fun getItem(p0: Int): Any {
       return p0
    }

    override fun getItemId(p0: Int): Long {
       return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var imagesPublication = ImageView(context)

        if (p1 == null) {
            imagesPublication = ImageView(context)
            imagesPublication.scaleType = ImageView.ScaleType.FIT_CENTER
            imagesPublication.layoutParams = AbsListView.LayoutParams(345,345)
        } else {
            imagesPublication = p1 as ImageView
        }

        Glide.with(context).load(images[p0]).centerCrop()
            .into(imagesPublication)

        return imagesPublication
    }

}