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
    private val context: HomeActivity // lien entre le fragment et l'activite
) : Fragment() {

    private var contactSearch: SearchView? = null // variable qui vas recuperer le champ de recherche
    private var publicationImage: GridView? = null // variable qui vas recuperer la grille
    private lateinit var adapterImage: PublicationAdapter // variable qui vas adapter un model a la grille
    val helpers = Helpers()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance() // recupere les l'information de l'utilisateur connecte

        // recuperation du fichier fragment search
        val viewFragmentSearch = inflater.inflate(R.layout.fragment_search, container, false)

        viewFragmentSearch?.findViewById<ProgressBar>(R.id.search_progressbar_load_data)?.visibility = View.VISIBLE

        // recuperation du champs de recherche et de la grille
        publicationImage = viewFragmentSearch?.findViewById(R.id.search_publication_list)
        contactSearch = viewFragmentSearch?.findViewById(R.id.search_publication)

        val repoPublication = PublicationRepository() // relation avec la bd
        // recupere la liste des publications
        repoPublication.initDataPublicationImage(null){
            if(publicationListImage.size > 0){
                adapterImage = PublicationAdapter(context, publicationListImage)
                publicationImage?.adapter = adapterImage
                viewFragmentSearch?.findViewById<ProgressBar>(R.id.search_progressbar_load_data)?.visibility = View.GONE
                publicationImage?.visibility = View.VISIBLE
                publicationImage?.setOnItemClickListener { adapterView, view, i, l ->

                    // on recupere les informations de la publication sur laquel on a clique
                    val imagesClick: ArrayList<PublicationModel?> = publicationListImage
                    var currentP = imagesClick[i]

                    // affiche sur une autre page toutes les publications de l'utilisateur qui a poster cette publication
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
        // verifie et affiche les elements correspondant a la recherche si le texte saisir correspond a un titre ou une description de publication
        repoPublication.AllDataSearchPublicationImage(p0.toString()){
            if(publicationListImage.size > 0){
                adapterImage =  PublicationAdapter(context, publicationListImage)
                publicationImage?.adapter = adapterImage
                adapterImage.notifyDataSetChanged()
            }
        }
    }

}