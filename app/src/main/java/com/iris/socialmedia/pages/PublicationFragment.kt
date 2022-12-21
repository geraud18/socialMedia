package com.iris.socialmedia.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.adapter.ImagePublicationAdapter
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.PublicationRepository.Singleton.downloadlPublicationImage
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationData
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PublicationFragment(
    private val context: HomeActivity
) : Fragment() {

    val helpers = Helpers()
    private var uploadedImagePublication: ImageView? = null
    private var file: Uri? = null
    private var type: String? = null
    private lateinit var gallery:GridView
    private lateinit var imageUri:Uri

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        imageUri = createUri()

        val viewFragmentPublication = inflater.inflate(R.layout.fragment_publication, container, false)
        gallery = viewFragmentPublication.findViewById(R.id.galleryGridView)
        val publicationShowCamera = viewFragmentPublication.findViewById<ImageView>(R.id.publication_show_camera)
        val publicationClose = viewFragmentPublication.findViewById<ImageView>(R.id.publication_close)

        val publicationBtnUpdate = viewFragmentPublication.findViewById<Button>(R.id.publication_btn_update)

        if(ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context,Array(1){Manifest.permission.READ_EXTERNAL_STORAGE},121)
        }

        publicationClose.setOnClickListener {
            context.onBackPressed()
        }

        publicationBtnUpdate.setOnClickListener { saveFormPublicationDatabase(viewFragmentPublication) }

        gallery.adapter = ImagePublicationAdapter(context,helpers.fetchGalleryImages(context))

        uploadedImagePublication = viewFragmentPublication.findViewById(R.id.publication_data)

        publicationShowCamera.setOnClickListener { pickImageCamera() }

        gallery.setOnItemClickListener { adapterView, view, i, l ->
            val imagesClick: ArrayList<String?> = helpers.fetchGalleryImages(context)
            file = Uri.parse(imagesClick[i].toString())
            type="normal"
            uploadedImagePublication?.setImageURI(file)
        }

        return viewFragmentPublication
    }

    private fun saveFormPublicationDatabase(viewFragmentPublication: View?) {
        val titlePublication = viewFragmentPublication?.findViewById<EditText>(R.id.publication_title)
        val descriptionPublication= viewFragmentPublication?.findViewById<EditText>(R.id.publication_description_or_legende)

        if(titlePublication?.text.toString().isNotEmpty() || descriptionPublication?.text.toString().isNotEmpty()){

            var btnPublication = viewFragmentPublication?.findViewById<Button>(R.id.publication_btn_update)
            var progressBarPublication = viewFragmentPublication?.findViewById<FrameLayout>(R.id.progressbar_publication)
            btnPublication?.visibility = View.INVISIBLE
            progressBarPublication?.visibility = View.VISIBLE

            val repoPublication = PublicationRepository()

            if(file != null){

                if(type == "normal"){
                    repoPublication.uploadImagePublication(file!!){
                        publicationData.data = downloadlPublicationImage.toString()
                        valuePublicationUpdate(viewFragmentPublication)
                        repoPublication.savePublication(publicationData)

                        progressBarPublication?.visibility = View.INVISIBLE
                        btnPublication?.visibility = View.VISIBLE

                        notifieSave(uploadedImagePublication, titlePublication, descriptionPublication)
                    }
                }else{
                    repoPublication.uploadImagePublicationCamera(file!!){
                        publicationData.data = downloadlPublicationImage.toString()
                        valuePublicationUpdate(viewFragmentPublication)
                        repoPublication.savePublication(publicationData)

                        progressBarPublication?.visibility = View.INVISIBLE
                        btnPublication?.visibility = View.VISIBLE

                        notifieSave(uploadedImagePublication,titlePublication,descriptionPublication)
                    }
                }
            }
            else{
                valuePublicationUpdate(viewFragmentPublication)
                repoPublication.savePublication(publicationData)

                progressBarPublication?.visibility = View.INVISIBLE
                btnPublication?.visibility = View.VISIBLE

                notifieSave(uploadedImagePublication, titlePublication, descriptionPublication)
            }

        }else{
            Toast.makeText(context,getString(R.string.notifie_publication_field_empty),Toast.LENGTH_SHORT).show()
        }
    }

    private fun notifieSave(uploadedImagePublicationReset: ImageView?, titlePublication: EditText?, descriptionPublication: EditText?) {
        titlePublication?.setText("")
        descriptionPublication?.setText("")
        file=null
        type="normal"
        publicationData.data =" "
        uploadedImagePublicationReset?.setImageResource(R.drawable.blank_image)
        Toast.makeText(context,getString(R.string.notifie_publication_save),Toast.LENGTH_SHORT).show()
    }

    private fun valuePublicationUpdate(viewFragmentPublication: View?) {
        val currentUser = firebaseAuth.currentUser
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentTime = formatter.format(time)
        if(currentUser != null){
            publicationData.id = UUID.randomUUID().toString()
            publicationData.id_users = currentUser.uid
            publicationData.title = viewFragmentPublication?.findViewById<EditText>(R.id.publication_title)?.text.toString()
            publicationData.description= viewFragmentPublication?.findViewById<EditText>(R.id.publication_description_or_legende)?.text.toString()
            publicationData.date = currentTime
        }
    }

    private val getResultCamera =
        registerForActivityResult(
            ActivityResultContracts.TakePicture() ) {
            if(it){
                uploadedImagePublication?.setImageURI(null)
                file = imageUri
                type="camera"
                uploadedImagePublication?.setImageURI(imageUri)

            } else{
                Toast.makeText(context,getString(R.string.notifie_file_empty),Toast.LENGTH_SHORT).show()
            }
        }

    private fun pickImageCamera() {
        getResultCamera.launch(imageUri)
    }

    private fun createUri() : Uri{
       var imageFile: File = File(context.filesDir,"camera_photo.jpg")
       return FileProvider.getUriForFile(
           context,
           "com.iris.socialmedia.fileProvider",
           imageFile
       )
    }

}