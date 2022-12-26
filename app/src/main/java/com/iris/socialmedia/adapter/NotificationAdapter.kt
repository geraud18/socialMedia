package com.iris.socialmedia.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iris.socialmedia.R
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.ContactModel
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.ContactRepository.Singleton.contactData
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationDataUser
import com.iris.socialmedia.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(
    private val context: HomeActivity,
    private val notificationList: ArrayList<ContactModel>,
    private val layoutId: Int) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    val helpers = Helpers()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage = view.findViewById<ImageView>(R.id.notification_user_image)
        val userName: TextView? = view.findViewById(R.id.notification_username)
        val dateValue: TextView? = view.findViewById(R.id.notification_date)
        val moreOption = view.findViewById<ImageView>(R.id.notification_more_option)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutId,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNotification = notificationList[position]
        val repoPublication = PublicationRepository()
        val repoContact = ContactRepository()

        repoPublication.getDataUser(currentNotification.guest_id){
            if(publicationDataUser.name == "" && publicationDataUser.firstname == ""){
                holder.userName?.text = "${publicationDataUser.username}"
            }
            else{
                holder.userName?.text = "${publicationDataUser.name}  ${publicationDataUser.firstname}"
            }
            val profileImage = holder.userImage
            if(publicationDataUser.profile?.isNotEmpty() == true){
                if (profileImage != null) {
                    Glide.with(context).load(Uri.parse(publicationDataUser.profile)).into(profileImage)
                    helpers.updateCircleImage(profileImage)
                }
            }
        }

        if(currentNotification.date != ""){
            val mDate2 = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(currentNotification.date)
            val date = Date()
            val current = SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)
            val mDate11 = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(current)
            val mDifference: Long = mDate11.time - mDate2.time

            val seconds: Long = mDifference / 1000
            val minutes: Long = seconds / 60
            val hours: Long = minutes / 60
            val days: Long = hours / 24
            if(days > 1){
                holder.dateValue?.text = context.getString(R.string.time_line_time_add)+" "+days.toString()+" "+context.getString(R.string.time_line_date_add)
            }else{
                if(hours >= 1){
                    holder.dateValue?.text = context.getString(R.string.time_line_time_add)+" "+hours.toString()+" "+context.getString(R.string.time_line_date_hours)
                }else{
                    if(minutes >= 1 || minutes <= 60){
                        holder.dateValue?.text = context.getString(R.string.time_line_time_add)+" "+minutes.toString()+" "+context.getString(R.string.time_line_date_minutes)
                    }else{
                        holder.dateValue?.text = context.getString(R.string.time_line_time_add)+" "+seconds.toString()+" "+context.getString(R.string.time_line_date_secondes)
                    }
                }
            }
        }

        holder.moreOption.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            dialog.setContentView(R.layout.popup_option_notification)
           // dialog.setCancelable(false)
            val imageProfileUser = dialog.findViewById<ImageView>(R.id.notification_image)
            val notificationRemove = dialog.findViewById<ImageView>(R.id.notification_remove)
            val notificationAccept = dialog.findViewById<ImageView>(R.id.notification_accept)
            var usernameFirst: String? = ""
            var usernameGuest: String? = ""

            repoPublication.getDataUser(currentNotification.guest_id){
                if(publicationDataUser.profile?.isNotEmpty() == true){
                    if(publicationDataUser.name == "" && publicationDataUser.firstname == ""){
                        usernameGuest = "${publicationDataUser.username}"
                    }
                    else{
                        usernameGuest = "${publicationDataUser.name}  ${publicationDataUser.firstname}"
                    }
                    if (imageProfileUser != null) {
                        Glide.with(context).load(Uri.parse(publicationDataUser.profile)).into(imageProfileUser)
                        helpers.updateCircleImage(imageProfileUser)
                    }
                }
            }

            repoPublication.getDataUser(currentNotification.user_id){
                if(publicationDataUser.profile?.isNotEmpty() == true){
                    if(publicationDataUser.name == "" && publicationDataUser.firstname == ""){
                        usernameFirst = "${publicationDataUser.username}"
                    }
                    else{
                        usernameFirst = "${publicationDataUser.name}  ${publicationDataUser.firstname}"
                    }
                }
            }

            notificationRemove?.setOnClickListener {
                repoContact.deleteContact(currentNotification)
                dialog.dismiss()
                notificationList.removeAt(position)
                this.notifyItemRemoved(position)
                context.invalidateMenu()
            }

            notificationAccept?.setOnClickListener {
                repoContact.acceptContact(currentNotification.user_id,currentNotification.guest_id,usernameGuest!!){
                    dialog.dismiss()

                    contactData.id = UUID.randomUUID().toString()
                    contactData.user_id = currentNotification.guest_id
                    contactData.guest_id = currentNotification.user_id
                    contactData.decision = "accpeter"
                    contactData.user_name_guest = usernameFirst!!

                    val time = Calendar.getInstance().time
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val currentTime = formatter.format(time)
                    contactData.date = currentTime

                    repoContact.saveContact(contactData)

                    notificationList.removeAt(position)
                    this.notifyItemRemoved(position)
                    context.invalidateMenu()
                    Toast.makeText(context,context.getString(R.string.account_user_invitation_accept),Toast.LENGTH_SHORT).show()
                }
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int = notificationList.size
}