package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.CommentAdapter
import com.iris.socialmedia.repository.EtatRepository
import com.iris.socialmedia.repository.EtatRepository.Singleton.etatData
import com.iris.socialmedia.repository.EtatRepository.Singleton.etatListComment
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.id_current_user
import java.text.SimpleDateFormat
import java.util.*

class CommentFragment(
    private val context: HomeActivity
) : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val viewFragmentComment = inflater.inflate(R.layout.fragment_comment, container, false)

        val publicationId = requireArguments().getString("publication_id")

        val listCommentRecycleView = viewFragmentComment?.findViewById<RecyclerView>(R.id.comment_publication_list)
        val commentSendButton = viewFragmentComment?.findViewById<ImageButton>(R.id.comment_send_button)
        val commentSendText = viewFragmentComment?.findViewById<EditText>(R.id.comment_send_text)


        var handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
                val repoEtat = EtatRepository()
                repoEtat.getListComment(publicationId.toString()) {
                    if(etatListComment.size > 0){
                        listCommentRecycleView?.adapter = CommentAdapter(context, etatListComment.sortedBy { it.date },R.layout.item_comment_publication)
                        listCommentRecycleView?.visibility = View.VISIBLE
                    }
                }
            }
        },500)

        val toolBarComment = viewFragmentComment?.findViewById<Toolbar>(R.id.top_bar_comment)
        toolBarComment?.setNavigationIcon(R.drawable.expandarrow)
        toolBarComment?.setNavigationOnClickListener {
            context.onBackPressed()
        }

        commentSendButton?.setOnClickListener {
            if(commentSendText?.text.toString() == ""){
                Toast.makeText(context,getString(R.string.notifie_publication_comment), Toast.LENGTH_SHORT).show()
            }else{
                val repoEtat = EtatRepository()
                etatData.id = UUID.randomUUID().toString()
                etatData.publication_id = publicationId.toString()
                etatData.user_id = id_current_user!!
                etatData.type = "comment"
                etatData.content = commentSendText?.text.toString()
                etatData.exist = "oui"

                val time = Calendar.getInstance().time
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val currentTime = formatter.format(time)
                etatData.date = currentTime

                repoEtat.saveEtatCommentUser(etatData)

                repoEtat.getListComment(publicationId.toString()) {
                    if(etatListComment.size > 0){
                        var commentAdapt = CommentAdapter(context, etatListComment.sortedBy { it.date },R.layout.item_comment_publication)
                        commentAdapt.notifyDataSetChanged()
                        listCommentRecycleView?.invalidate()
                        listCommentRecycleView?.adapter = commentAdapt
                        listCommentRecycleView?.visibility = View.VISIBLE
                        commentSendText?.setText("")
                    }
                }
            }
        }

      //  viewFragmentComment.findViewById<TextView>(R.id.testparam).text = strtext.toString()

        return viewFragmentComment
    }

}