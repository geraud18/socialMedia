package com.iris.socialmedia.adapter

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iris.socialmedia.R
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.PublicationModel
import com.iris.socialmedia.pages.CommentFragment
import com.iris.socialmedia.pages.EditAccountFragment
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.pages.PopupOption
import com.iris.socialmedia.repository.EtatRepository
import com.iris.socialmedia.repository.EtatRepository.Singleton.countComment
import com.iris.socialmedia.repository.EtatRepository.Singleton.countFavorite
import com.iris.socialmedia.repository.EtatRepository.Singleton.etatData
import com.iris.socialmedia.repository.EtatRepository.Singleton.etatUserFavorite
import com.iris.socialmedia.repository.EtatRepository.Singleton.etatUserFavoriteExist
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationDataUser
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user
import java.text.SimpleDateFormat
import java.util.*


class TimeLineAdapter(
    val context: HomeActivity,
    private val timeLineList: List<PublicationModel>,
    private val layoutId: Int) : RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {

    val helpers = Helpers()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeLineUserImage = view.findViewById<ImageView>(R.id.time_line_user_image)
        val timeLineMoreOption = view.findViewById<ImageView>(R.id.time_line_more_option)
        val timeLineData = view.findViewById<ImageView>(R.id.time_line_data)
        val timeLineFavorite = view.findViewById<ImageView>(R.id.time_line_favorite)
        val timeLineComment = view.findViewById<ImageView>(R.id.time_line_comment)
        val timeLineUsername: TextView? = view.findViewById(R.id.time_line_username)
        val timeLineDate: TextView? = view.findViewById(R.id.time_line_date)
        val timeLineDescription: TextView? = view.findViewById(R.id.time_line_description)
        val timeLineTimeAdd: TextView? = view.findViewById(R.id.time_line_time_add)
        val timeLineTitle: TextView? = view.findViewById(R.id.time_line_title)
        val timeLineFavoriteNumber: TextView? = view.findViewById(R.id.time_line_favorite_number)
        val timeLineCommentNumber: TextView? = view.findViewById(R.id.time_line_comment_number)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutId,parent,false)

        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // RECUPERER LES DONNEES

        val currentPublication = timeLineList[position]
        val repoPublication = PublicationRepository()
        val repoEtat = EtatRepository()


        if(currentPublication.data != ""){
            Glide.with(context)
                .load(Uri.parse(currentPublication.data))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.timeLineData)
        }

        repoPublication.getDataUser(currentPublication.id_users){
            if(publicationDataUser.name == "" && publicationDataUser.firstname == ""){
                holder.timeLineUsername?.text = "${publicationDataUser.username}"
            }
            else{
                holder.timeLineUsername?.text = "${publicationDataUser.name}  ${publicationDataUser.firstname}"
            }
            val profileImage = holder.timeLineUserImage
            if(publicationDataUser.profile?.isNotEmpty() == true){
                if (profileImage != null) {
                    Glide.with(context).load(Uri.parse(publicationDataUser.profile)).into(profileImage)
                    helpers.updateCircleImage(profileImage)
                }
            }
        }

        repoEtat.countDataFavoriteUser(currentPublication.id,id_current_user!!){
            if(etatUserFavorite){
                holder.timeLineFavorite.setColorFilter(context.resources.getColor(R.color.figma_color_error))
            }else{
                holder.timeLineFavorite.setColorFilter(context.resources.getColor(R.color.black))
            }
        }

        repoEtat.countDataFavoriteComment(currentPublication.id){
            holder.timeLineFavoriteNumber?.text = countFavorite.toString()
            holder.timeLineCommentNumber?.text = countComment.toString()
        }

      //  Glide.with(context).load(Uri.parse(currentPublication.data)).into(holder.timeLineData)

        if(currentPublication.title?.isNotEmpty() == true){
            holder.timeLineTitle?.text = currentPublication.title
        }else{
            holder.timeLineTitle?.visibility = View.GONE
        }

        if(currentPublication.description?.isNotEmpty() == true){
            holder.timeLineDescription?.text = currentPublication.description
        }else{
            holder.timeLineDescription?.visibility = View.GONE
        }

        val time1 = Calendar.getInstance().time

        if(currentPublication.date != ""){
            val mDate2 = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(currentPublication.date)

            val date = Date()
            val current = SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)
            val mDate11 = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(current)
            val mDifference: Long = mDate11.time - mDate2.time

          //  val mDifferenceDates: Long = mDifference / (24 * 60 * 60 * 1000)
            val seconds: Long = mDifference / 1000
            val minutes: Long = seconds / 60
            val hours: Long = minutes / 60
            val days: Long = hours / 24
          //  val mDayDifference = "${} ${getMinutes(minutes)}"
            if(days > 1){
                holder.timeLineTimeAdd?.text = context.getString(R.string.time_line_time_add)+" "+days.toString()+" "+context.getString(R.string.time_line_date_add)
            }else{
                if(hours >= 1 || hours <= 24){
                    holder.timeLineTimeAdd?.text = context.getString(R.string.time_line_time_add)+" "+hours.toString()+" "+context.getString(R.string.time_line_date_hours)
                }else{
                    if(minutes >= 1 || minutes <= 60){
                        holder.timeLineTimeAdd?.text = context.getString(R.string.time_line_time_add)+" "+minutes.toString()+" "+context.getString(R.string.time_line_date_minutes)
                    }else{
                        holder.timeLineTimeAdd?.text = context.getString(R.string.time_line_time_add)+" "+seconds.toString()+" "+context.getString(R.string.time_line_date_secondes)
                    }
                }
            }
            holder.timeLineTimeAdd?.visibility = View.VISIBLE
        }

        holder.timeLineDate?.text = currentPublication.date

        holder.timeLineFavorite.setOnClickListener {

            repoEtat.countDataFavoriteUserExist(currentPublication.id,id_current_user!!){
                if(etatUserFavoriteExist){
                    repoEtat.countDataFavoriteUser(currentPublication.id,id_current_user!!) {
                        if (etatUserFavorite) {
                            repoEtat.saveEtatFavoriteUserLike(currentPublication.id,id_current_user!!){
                                repoEtat.countDataFavoriteComment(currentPublication.id){
                                    holder.timeLineFavoriteNumber?.text = countFavorite.toString()
                                    holder.timeLineFavorite.setColorFilter(context.resources.getColor(R.color.black))
                                }
                            }
                        } else {
                            repoEtat.saveEtatFavoriteUserUnLike(currentPublication.id,id_current_user!!){
                                repoEtat.countDataFavoriteComment(currentPublication.id){
                                    holder.timeLineFavoriteNumber?.text = countFavorite.toString()
                                    holder.timeLineFavorite.setColorFilter(context.resources.getColor(R.color.figma_color_error))
                                }
                            }
                        }
                    }
                }else{
                    etatData.id = UUID.randomUUID().toString()
                    etatData.publication_id = currentPublication.id
                    etatData.user_id = id_current_user!!
                    etatData.type = "favorite"
                    etatData.content = "like"
                    etatData.exist = "oui"

                    val time = Calendar.getInstance().time
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val currentTime = formatter.format(time)
                    etatData.date = currentTime

                    repoEtat.saveEtatFavoriteUser(etatData)
                    repoEtat.countDataFavoriteComment(currentPublication.id){
                        holder.timeLineFavoriteNumber?.text = countFavorite.toString()
                        holder.timeLineFavorite.setColorFilter(context.resources.getColor(R.color.figma_color_error))
                    }
                }
            }
        }

        holder.timeLineMoreOption.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            dialog.setContentView(R.layout.popup_option_item)
            dialog.show()
        }

        holder.timeLineComment.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("publication_id", currentPublication.id)

            val commentFragment: Fragment = CommentFragment(context)
            commentFragment.arguments = bundle
            val fragmentManager: FragmentManager = context.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
            fragmentTransaction.replace(R.id.homme_activity, commentFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        holder.timeLineUserImage.setOnClickListener {
            helpers.viewProfile(context,currentPublication.id_users)
        }

        holder.timeLineUsername?.setOnClickListener {
            helpers.viewProfile(context,currentPublication.id_users)
        }

    }

    override fun getItemCount(): Int = timeLineList.size

}