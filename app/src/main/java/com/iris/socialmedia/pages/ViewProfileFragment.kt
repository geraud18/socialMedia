package com.iris.socialmedia.pages

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.ImagePublicationAdapter
import com.iris.socialmedia.adapter.PublicationAdapter
import com.iris.socialmedia.adapter.TimeLineAdapter
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.PublicationModel
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.ContactRepository.Singleton.contactData
import com.iris.socialmedia.repository.ContactRepository.Singleton.etatInvitationSend
import com.iris.socialmedia.repository.EtatRepository
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationDataUser
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationListImage
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user
import java.text.SimpleDateFormat
import java.util.*

class ViewProfileFragment(
    private val context: HomeActivity
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()
        
        val viewFragmentProfile = inflater.inflate(R.layout.fragment_view_profile, container, false)
        
        val idUser = requireArguments().getString("id_user")

        val repoContact = ContactRepository()

        val profileBtnModifierProfil = viewFragmentProfile?.findViewById<Button>(R.id.view_profile_btn_modifier_profil)

        repoContact.checkInvitationSend(context,idUser.toString(),id_current_user!!){
            if(etatInvitationSend != ""){
                profileBtnModifierProfil?.hint = etatInvitationSend
                profileBtnModifierProfil?.isEnabled = true
            }
        }

        showValueUser(viewFragmentProfile,idUser)

        val topBaProfileUser = viewFragmentProfile?.findViewById<Toolbar>(R.id.top_bar_profile_user)
        topBaProfileUser?.setNavigationIcon(R.drawable.custumleftarrow)
        topBaProfileUser?.setNavigationOnClickListener {
            context.onBackPressed()
        }

        profileBtnModifierProfil?.setOnClickListener {
            repoContact.checkInvitationSend(context,idUser.toString(),id_current_user!!){
                if(etatInvitationSend == ""){
                    var progressBarPublication = viewFragmentProfile?.findViewById<FrameLayout>(R.id.progressbar_invitation)
                    profileBtnModifierProfil?.visibility = View.INVISIBLE
                    progressBarPublication?.visibility = View.VISIBLE

                    contactData.id = UUID.randomUUID().toString()
                    contactData.user_id = idUser.toString()
                    contactData.guest_id = id_current_user!!
                    contactData.decision = "en attentes"

                    val time = Calendar.getInstance().time
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val currentTime = formatter.format(time)
                    contactData.date = currentTime

                    var handler = Handler()
                    handler.postDelayed(object :Runnable{
                        override fun run(){
                                repoContact.saveContact(contactData)
                                progressBarPublication?.visibility = View.INVISIBLE
                                profileBtnModifierProfil?.hint = context.getString(R.string.account_user_invitation)
                                profileBtnModifierProfil?.visibility = View.VISIBLE
                                profileBtnModifierProfil?.isEnabled = true
                        }
                    },1000)
                }
            }
        }

        return viewFragmentProfile
    }

    private fun showValueUser(viewFragmentProfile: View?, idUser: String?) {
        val repoPublication = PublicationRepository()
        val helpers = Helpers()

        repoPublication.getDataUser(idUser.toString()){
            if(publicationDataUser.name == "" && publicationDataUser.firstname == ""){
                viewFragmentProfile?.findViewById<TextView>(R.id.view_profile_username)?.text = "${publicationDataUser.username}"
            }
            else{
                viewFragmentProfile?.findViewById<TextView>(R.id.view_profile_username)?.text = "${publicationDataUser.name}  ${publicationDataUser.firstname}"
            }
            val profileImage = viewFragmentProfile?.findViewById<ImageView>(R.id.view_profile_image)
            if(publicationDataUser.profile?.isNotEmpty() == true){
                if (profileImage != null) {
                    Glide.with(context).load(Uri.parse(publicationDataUser.profile)).into(profileImage)
                    helpers.updateCircleImage(profileImage)
                }
            }
            val imageCheckOnline = viewFragmentProfile?.findViewById<ImageView>(R.id.view_profile_check_online)
            if(publicationDataUser.connect == "true"){
                imageCheckOnline?.setColorFilter(context.resources.getColor(R.color.figma_color_success))
                imageCheckOnline?.visibility = View.VISIBLE
            }else{
                imageCheckOnline?.setColorFilter(context.resources.getColor(R.color.figma_color_account_profile2))
                imageCheckOnline?.visibility = View.VISIBLE
            }
        }

        viewFragmentProfile?.findViewById<ProgressBar>(R.id.view_profile_progressbar_load_data)?.visibility = View.VISIBLE

        val publicationImage = viewFragmentProfile?.findViewById<GridView>(R.id.view_profile_publication_gridView)
        repoPublication.initDataPublicationImage(idUser.toString()) {

            if(publicationListImage.size > 0){
                publicationImage?.adapter = PublicationAdapter(context, publicationListImage)
                viewFragmentProfile?.findViewById<ProgressBar>(R.id.view_profile_progressbar_load_data)?.visibility = View.GONE
                publicationImage?.visibility = View.VISIBLE

            }else{
                viewFragmentProfile?.findViewById<ProgressBar>(R.id.view_profile_progressbar_load_data)?.visibility = View.GONE
                publicationImage?.visibility = View.GONE
                viewFragmentProfile?.findViewById<TextView>(R.id.view_profile_notifie_data)?.visibility = View.VISIBLE
            }
        }

        if(idUser == id_current_user){
          viewFragmentProfile?.findViewById<Button>(R.id.view_profile_btn_modifier_profil)?.visibility = View.GONE
        }

    }
}