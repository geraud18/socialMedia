package com.iris.socialmedia.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iris.socialmedia.R
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.EtatModel
import com.iris.socialmedia.model.PublicationModel
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationDataUser

class CommentAdapter(
    val context: HomeActivity,
    private val etatListComment: List<EtatModel>,
    private val layoutId: Int) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    val helpers = Helpers()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage = view.findViewById<ImageView>(R.id.comment_plublication_user_image)
        val userName: TextView? = view.findViewById(R.id.comment_plublication_username)
        val commentValue: TextView? = view.findViewById(R.id.comment_plublication_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutId,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentComment = etatListComment[position]
        val repoPublication = PublicationRepository()

        repoPublication.getDataUser(currentComment.user_id){
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
            holder.commentValue?.text = currentComment.content
        }
    }

    override fun getItemCount(): Int = etatListComment.size
}