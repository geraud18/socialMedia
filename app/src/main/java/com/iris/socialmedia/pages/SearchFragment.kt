package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.ContactAdapter
import com.iris.socialmedia.adapter.ImagePublicationAdapter
import com.iris.socialmedia.adapter.PublicationAdapter
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.PublicationModel
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationListImage
import com.iris.socialmedia.repository.PublicationRepository.Singleton.searchPublicationListImage
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth


class SearchFragment(
    private val context: HomeActivity
) : Fragment() {

    private var contactSearch: SearchView? = null
    private var publicationImage: GridView? = null
    private lateinit var adapterImage: PublicationAdapter
    val helpers = Helpers()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val viewFragmentSearch = inflater.inflate(R.layout.fragment_search, container, false)

        viewFragmentSearch?.findViewById<ProgressBar>(R.id.search_progressbar_load_data)?.visibility = View.VISIBLE

        publicationImage = viewFragmentSearch?.findViewById(R.id.search_publication_list)
        contactSearch = viewFragmentSearch?.findViewById(R.id.search_publication)

        val repoPublication = PublicationRepository()
        repoPublication.initDataPublicationImage(null){
            if(publicationListImage.size > 0){
                adapterImage = PublicationAdapter(context, publicationListImage)
                publicationImage?.adapter = adapterImage
                viewFragmentSearch?.findViewById<ProgressBar>(R.id.search_progressbar_load_data)?.visibility = View.GONE
                publicationImage?.visibility = View.VISIBLE
                publicationImage?.setOnItemClickListener { adapterView, view, i, l ->

                    val imagesClick: ArrayList<PublicationModel?> = publicationListImage
                    var currentP = imagesClick[i]

                    helpers.getListTimeLineUser(context,currentP?.id_users)

                }
            }else{
                viewFragmentSearch?.findViewById<ProgressBar>(R.id.search_progressbar_load_data)?.visibility = View.GONE
                publicationImage?.visibility = View.GONE
            }
        }

        contactSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                filterContact(p0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                filterContact(p0)
                return true
            }
        })

        return viewFragmentSearch
    }

    private fun filterContact(p0: String?) {
        val repoPublication = PublicationRepository()
        repoPublication.AllDataSearchPublicationImage(p0.toString()){
            if(publicationListImage.size > 0){
                adapterImage =  PublicationAdapter(context, publicationListImage)
                publicationImage?.adapter = adapterImage
                adapterImage.notifyDataSetChanged()
            }
        }
    }

}