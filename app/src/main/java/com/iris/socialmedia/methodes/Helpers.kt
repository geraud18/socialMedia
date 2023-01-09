package com.iris.socialmedia.methodes

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.iris.socialmedia.R
import com.iris.socialmedia.pages.*
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.userData
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Helpers : AppCompatActivity() {


    fun verifEmail(emailParams: EditText) : Boolean {

        val regex = "^[A-Za-z0-9+_.-]+@(.+)\$"
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(emailParams.text.toString())
        return matcher.matches()
    }

    fun verifPassword(passwordParams:EditText) : Boolean{

        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(passwordParams.text.toString())

        return matcher.matches()
    }

    fun startApplication(mActivity: Activity,callback: () -> Unit){
        val repoUser = UserRepository()
        repoUser.initDataUser(){
            var locale = Locale(userData.language)
            val res = mActivity.resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.setLocale(locale)
            res.updateConfiguration(conf, dm)
            callback()
        }

    }

    fun viewProfile(context: HomeActivity,id_user: String){
        val bundle = Bundle()
        bundle.putString("id_user", id_user)
        val viewProfileFragment: Fragment = ViewProfileFragment(context)
        viewProfileFragment.arguments = bundle

        val fragmentManager: FragmentManager = context.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.slide_right, R.anim.slide_left,
            R.anim.slide_right, R.anim.slide_left)
        fragmentTransaction.replace(R.id.homme_activity, viewProfileFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun updateCircleImage(imageProfile: ImageView) {

        imageProfile.layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
        imageProfile.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT

        val marginParams = imageProfile.layoutParams as ViewGroup.MarginLayoutParams
        marginParams.setMargins(0, 0, 0, 0)

        imageProfile.scaleType = ImageView.ScaleType.CENTER_CROP;
    }

    fun fetchGalleryImages(context: HomeActivity): ArrayList<String?> {

        var imagelist: Cursor

        val galleryImageUrls: ArrayList<String?>
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
      //  val orderBy2 = MediaStore.Video.Media.DATE_TAKEN
        var cols = listOf<String>(MediaStore.Images.Thumbnails.DATA).toTypedArray()
      //  var cols2 = listOf<String>(MediaStore.Video.Thumbnails.DATA).toTypedArray()
        imagelist = context?.contentResolver?.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            cols, null, null, "$orderBy DESC")!!

        galleryImageUrls = ArrayList()
        if (imagelist != null) {
            imagelist.moveToFirst()
            while (!imagelist.isAfterLast()) {
                val dataColumnIndex: Int =
                    imagelist.getColumnIndex(MediaStore.Images.Media.DATA) //get column index
                galleryImageUrls.add(imagelist.getString(dataColumnIndex)) //get Image from column index
                imagelist.moveToNext()
            }
        }

        return galleryImageUrls
    }

    fun getListTimeLineUser(context: HomeActivity,user_id: String?) {

        val bundle = Bundle()
        bundle.putString("user_id", user_id)

        val timeLineUserFragment: Fragment = TimeLineUserFragment(context)
        timeLineUserFragment.arguments = bundle
        val fragmentManager: FragmentManager = context.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
        fragmentTransaction.replace(R.id.homme_activity, timeLineUserFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}