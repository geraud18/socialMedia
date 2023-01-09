package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.ImagePublicationAdapter
import com.iris.socialmedia.adapter.PublicationAdapter
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.PublicationModel
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationList
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationListImage
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user
import com.iris.socialmedia.repository.UserRepository.Singleton.userData


class AccountFragment(
    private val context: HomeActivity
) : Fragment() {

    val helpers = Helpers()

    override fun onViewCreated(view: View, saveInstanceState: Bundle ?) {
        super.onViewCreated(view, saveInstanceState)
        postponeEnterTransition()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val viewFragmentAccount = inflater.inflate(R.layout.fragment_account, container, false)

        val btnGetEditAccount = viewFragmentAccount.findViewById<TextView>(R.id.account_btn_modifier_profil)

        btnGetEditAccount.setOnClickListener {

            val editAccountFragment: Fragment = EditAccountFragment(context)
            val fragmentManager: FragmentManager = context.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.slide_right, R.anim.slide_left,R.anim.slide_right, R.anim.slide_left)
            fragmentTransaction.replace(R.id.homme_activity, editAccountFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        showValueUser(viewFragmentAccount)

        val accountImage = viewFragmentAccount.findViewById<ImageView>(R.id.account_image)

        accountImage?.setOnClickListener { bigImage(accountImage) }

        val accountBtnAddPublication = viewFragmentAccount.findViewById<ImageView>(R.id.account_btn_add_publication)
        accountBtnAddPublication.setOnClickListener {

            val publicationFragment: Fragment = PublicationFragment(context)
            val fragmentManager: FragmentManager = context.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
            fragmentTransaction.replace(R.id.homme_activity, publicationFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }


        val swipeRefreshLayout = viewFragmentAccount?.findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val refreshNameUser = viewFragmentAccount?.findViewById<TextView>(R.id.account_username)

        swipeRefreshLayout?.setOnRefreshListener{
            refreshNameUser?.text = refreshName()

            val profileImage = viewFragmentAccount?.findViewById<ImageView>(R.id.account_image)
            if(userData.profile?.isNotEmpty() == true){
                if (profileImage != null) {
                    Glide.with(this).load(Uri.parse(userData.profile)).into(profileImage)
                    helpers.updateCircleImage(profileImage)
                }
            }

            swipeRefreshLayout.isRefreshing = false
        }

        return viewFragmentAccount
    }


    private fun bigImage(imageProfile: ImageView) {
        if(userData.profile?.isNotEmpty() == true){

            val bigImageFragment: Fragment = BigImageFragment(context)
            val fragmentManager: FragmentManager = context.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.animator.scale_up, R.animator.scale_down,R.animator.scale_up, R.animator.scale_down)
            fragmentTransaction.replace(R.id.homme_activity, bigImageFragment)
            fragmentTransaction.addSharedElement(imageProfile, imageProfile.transitionName)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }


    private fun showValueUser(viewFragmentAccount: View?) {

        if(userData.name == "" && userData.firstname == ""){
            viewFragmentAccount?.findViewById<TextView>(R.id.account_username)?.text = "${userData.username}"
        }
        else{
            viewFragmentAccount?.findViewById<TextView>(R.id.account_username)?.text = "${userData.name}  ${userData.firstname}"
        }

        val profileImage = viewFragmentAccount?.findViewById<ImageView>(R.id.account_image)
        if(userData.profile?.isNotEmpty() == true){
            if (profileImage != null) {
                Glide.with(this).load(Uri.parse(userData.profile)).into(profileImage)
                helpers.updateCircleImage(profileImage)
            }
        }

            viewFragmentAccount?.findViewById<ProgressBar>(R.id.progressbar_load_data)?.visibility = View.VISIBLE

            val publicationImage = viewFragmentAccount?.findViewById<GridView>(R.id.account_publication_gridView)
            val repoPublication = PublicationRepository()

            repoPublication.initDataPublicationImage(id_current_user!!) {

                if(publicationListImage.size > 0){
                    publicationImage?.adapter = PublicationAdapter(context,publicationListImage)
                    viewFragmentAccount?.findViewById<ProgressBar>(R.id.progressbar_load_data)?.visibility = View.GONE
                    publicationImage?.visibility = View.VISIBLE
                    publicationImage?.setOnItemClickListener { adapterView, view, i, l ->

                        val imagesClick: ArrayList<PublicationModel?> = publicationListImage
                        var currentP = imagesClick[i]

                        helpers.getListTimeLineUser(context,currentP?.id_users)

                    }
                }else{
                    viewFragmentAccount?.findViewById<ProgressBar>(R.id.progressbar_load_data)?.visibility = View.GONE
                    publicationImage?.visibility = View.GONE
                    viewFragmentAccount?.findViewById<TextView>(R.id.account_notifie_data)?.visibility = View.VISIBLE
                }

            }
    }

    fun refreshName() : String{
        if(userData.name == "" && userData.firstname == ""){
            return "${userData.username}"
        }
        else{
            return "${userData.name}  ${userData.firstname}"
        }
    }

}