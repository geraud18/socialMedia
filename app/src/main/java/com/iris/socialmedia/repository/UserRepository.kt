package com.iris.socialmedia.repository

import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.iris.socialmedia.model.UserModel
import com.iris.socialmedia.repository.UserRepository.Singleton.dataBaseReferenceUser
import com.iris.socialmedia.repository.UserRepository.Singleton.downloadlProfileImage
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.storageReference
import com.iris.socialmedia.repository.UserRepository.Singleton.userData


class UserRepository {

    object Singleton {

        // LIEN POUR ACCEDER A NOTRE BOITE DE STOCKAGE (BUCKET)
        private val BLUCKET_URL:String = "gs://social-network-2b790.appspot.com"

        lateinit var firebaseAuth: FirebaseAuth

        // SE CONECTER A NOTRE ESPACE DE STOCKAGE
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(BLUCKET_URL)

        val dataBaseReferenceUser = FirebaseDatabase.getInstance().getReference("users")

        var downloadlProfileImage:Uri? = null

        var userData = UserModel("","","","","","","","","","","")

    }

    fun initDataUser(callback: () -> Unit){

        // METTRE LES DONNEES DE NOTRE UTILISATEUR DANS LA L'OBJET USERDATA
        dataBaseReferenceUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentuser = firebaseAuth.currentUser
                if (currentuser != null) {
                    for(ds in snapshot.children){
                        // CONSTRUIRE UN OBJET USER
                        val data = ds.getValue(UserModel::class.java)
                        // VERIFIER QUE L'UTILISATEUR N'EST PAS NULL
                        if(data != null && data.id == currentuser.uid ){
                            // METTRE A OUR MON OBJET
                            userData = data
                        }
                    }
                }

                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateUser(userModel: UserModel) {
        val currentuser = firebaseAuth.currentUser
        if (currentuser != null) {
            dataBaseReferenceUser.child(currentuser.uid).setValue(userModel)
        }
    }

    fun uploadImageProfile(file: Uri, callback: () -> Unit ){
        // VERIFIER QUE LE FICHIER N'EST PAS NULL
        if(file != null){

            var filename = file.lastPathSegment
            filename = filename?.substring(filename.lastIndexOf("/") + 1)

            val ref = storageReference.child(filename.toString())
            val uploadTask = ref.putFile(file)

                // DEMARRER L'ENVOIE
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> {
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
                        downloadlProfileImage = task.result
                        callback()
                    }
                }
        }
    }

}