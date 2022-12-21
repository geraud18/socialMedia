package com.iris.socialmedia.repository

import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iris.socialmedia.model.EtatModel
import com.iris.socialmedia.model.PublicationModel
import com.iris.socialmedia.repository.EtatRepository.Singleton.countComment
import com.iris.socialmedia.repository.EtatRepository.Singleton.countFavorite
import com.iris.socialmedia.repository.EtatRepository.Singleton.dataBaseReferenceEtat
import com.iris.socialmedia.repository.EtatRepository.Singleton.etatListComment
import com.iris.socialmedia.repository.EtatRepository.Singleton.etatUserFavorite
import com.iris.socialmedia.repository.EtatRepository.Singleton.etatUserFavoriteExist
import java.util.*


class EtatRepository {

    object Singleton {

        val dataBaseReferenceEtat = FirebaseDatabase.getInstance().getReference("etats")
        var etatUserFavorite:Boolean = false
        var etatUserFavoriteExist:Boolean = false
        var countFavorite:Int = 0
        var countComment:Int = 0
        var etatData = EtatModel("","","","","","","")
        val etatListComment = arrayListOf<EtatModel>()
    }

    fun getListComment(publication_id : String,callback: () -> Unit){
        dataBaseReferenceEtat.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                etatListComment.clear()
                for(ds in snapshot.children){
                    val listComment = ds.getValue(EtatModel::class.java)
                    if(listComment != null){
                        if(listComment.publication_id == publication_id && listComment.type == "comment"){
                            etatListComment.add(listComment)
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun saveEtatFavoriteUserLike(publication_id : String, user_id : String,callback: () -> Unit){
        dataBaseReferenceEtat.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var find:Boolean = false
                for(ds in snapshot.children){
                    val etat = ds.getValue(EtatModel::class.java)
                    if(etat != null){
                        if(etat.publication_id == publication_id && etat.type == "favorite" && etat.content == "like" && etat.user_id == user_id){
                           // find = true
                            val map = mutableMapOf<String, String>()
                            map["content"] = "unlike"
                            ds.ref.updateChildren(map as Map<String, Any>)
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun saveEtatFavoriteUserUnLike(publication_id : String, user_id : String,callback: () -> Unit){
        dataBaseReferenceEtat.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val etat = ds.getValue(EtatModel::class.java)
                    if(etat != null){
                        if(etat.publication_id == publication_id && etat.type == "favorite" && etat.content == "unlike" && etat.user_id == user_id){
                           // find = true
                            val map = mutableMapOf<String, String>()
                            map["content"] = "like"
                            ds.ref.updateChildren(map as Map<String, Any>)
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun countDataFavoriteUser(publication_id : String, user_id : String,callback: () -> Unit) {
        dataBaseReferenceEtat.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                etatUserFavorite = false
                for(ds in snapshot.children){
                    val etat = ds.getValue(EtatModel::class.java)
                    if(etat != null){
                        if(etat.publication_id == publication_id && etat.type == "favorite" && etat.content == "like" && etat.user_id == user_id){
                            etatUserFavorite = true
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun countDataFavoriteUserExist(publication_id : String, user_id : String,callback: () -> Unit) {
        dataBaseReferenceEtat.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                etatUserFavoriteExist = false
                for(ds in snapshot.children){
                    val etat = ds.getValue(EtatModel::class.java)
                    if(etat != null){
                        if(etat.publication_id == publication_id && etat.type == "favorite" && etat.exist == "oui" && etat.user_id == user_id){
                            etatUserFavoriteExist = true
                        }
                    }
                }
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun saveEtatFavoriteUser(etatModel: EtatModel) = dataBaseReferenceEtat.child(etatModel.id).setValue(etatModel)
    fun saveEtatCommentUser(etatModel: EtatModel) = dataBaseReferenceEtat.child(etatModel.id).setValue(etatModel)

    fun countDataFavoriteComment(publication_id : String,callback: () -> Unit){
        var countF:Int = 0
        var countC:Int = 0
        dataBaseReferenceEtat.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val etat = ds.getValue(EtatModel::class.java)
                    if(etat != null){
                        if(etat.publication_id == publication_id && etat.type == "favorite" && etat.content == "like"){
                            countF++
                        }
                        if(etat.publication_id == publication_id && etat.type == "comment"){
                            countC++
                        }
                    }
                }
                countFavorite = countF
                countComment = countC
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}