package com.iris.socialmedia.repository

import android.annotation.SuppressLint
import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.iris.socialmedia.model.PublicationModel
import com.iris.socialmedia.model.UserModel
import com.iris.socialmedia.repository.PublicationRepository.Singleton.dataBaseReferencePublication
import com.iris.socialmedia.repository.PublicationRepository.Singleton.downloadlPublicationImage
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationDataUser
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationList
import com.iris.socialmedia.repository.PublicationRepository.Singleton.publicationListImage
import com.iris.socialmedia.repository.PublicationRepository.Singleton.storageReferencePublication
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import java.io.File
import java.util.*

class PublicationRepository {

    object Singleton {

      // SE CONECTER A NOTRE ESPACE DE STOCKAGE
      val storageReferencePublication: StorageReference = FirebaseStorage.getInstance().reference.child("publicationImage")

      val dataBaseReferencePublication = FirebaseDatabase.getInstance().getReference("publications")

      var downloadlPublicationImage: Uri? = null

      var publicationData = PublicationModel("","","","","","")

      var publicationDataUser = UserModel("","","","","","","","","","","","")

      val publicationList = arrayListOf<PublicationModel>()

      var publicationListImage = ArrayList<String?>()

    }

    fun initDataPublication(callback: () -> Unit){
        dataBaseReferencePublication.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                publicationList.clear()
                for(ds in snapshot.children){
                    val publication = ds.getValue(PublicationModel::class.java)
                    if(publication != null){
                        if(publication.id != ""){
                            publicationList.add(publication)
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun initDataPublicationImage(id_user:String, callback: () -> Unit){
        dataBaseReferencePublication.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentuser = firebaseAuth.currentUser
                publicationListImage.clear()
                if (currentuser != null) {
                    for(ds in snapshot.children){
                        val publication = ds.getValue(PublicationModel::class.java)
                        if(publication != null){
                            if (publication.data != "" && publication.id_users == id_user){
                                publicationListImage.add(publication.data)
                            }
                        }
                    }
                }
                publicationListImage.removeAll(listOf("", null))
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getDataUser(user_id: String,callback: () -> Unit){
        UserRepository.Singleton.dataBaseReferenceUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val data = ds.getValue(UserModel::class.java)
                    if(data != null && data.id == user_id ){
                        publicationDataUser = data
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @SuppressLint("SuspiciousIndentation")
    fun uploadImagePublication(file: Uri, callback: () -> Unit ){
        // VERIFIER QUE LE FICHIER N'EST PAS NULL
        if(file != null){

            var files = Uri.fromFile(File(file.toString()))
            val ref = storageReferencePublication.child(file.toString())

            val uploadTask = ref.putFile(files)

            // DEMARRER L'ENVOIE
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                    task ->
                // VERIFIER SI IL A UN PROBLEME LORS DE L'ENVOI
                if(!task.isSuccessful){
                    task.exception?.let{ throw it }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task->
                // VERIFIER SI TOUS A FONCTIONNER
                if(task.isSuccessful){
                    // RECUPERER L'IMAGE
                    downloadlPublicationImage = task.result
                    callback()
                }
            }
        }
    }

    fun uploadImagePublicationCamera(file: Uri, callback: () -> Unit ){
        // VERIFIER QUE LE FICHIER N'EST PAS NULL
        if(file != null){

            val filename = UUID.randomUUID().toString() + ".jpg"
            val ref = storageReferencePublication.child(filename)

            val uploadTask = ref.putFile(file)

            // DEMARRER L'ENVOIE
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                    task ->
                // VERIFIER SI IL A UN PROBLEME LORS DE L'ENVOI
                if(!task.isSuccessful){
                    task.exception?.let{ throw it }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task->
                // VERIFIER SI TOUS A FONCTIONNER
                if(task.isSuccessful){
                    // RECUPERER L'IMAGE
                    downloadlPublicationImage = task.result
                    callback()
                }
            }
        }
    }

    fun savePublication(publicationModel: PublicationModel) = dataBaseReferencePublication.child(publicationModel.id).setValue(publicationModel)

}