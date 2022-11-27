package com.iris.socialmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.auth.RegisterActivity
import com.iris.socialmedia.methodes.LoadingDialog
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.userData
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if(currentUser != null){
            // CHARGER LES DONNEES DE L'UTILISATEUR

            val repoUser = UserRepository()
            repoUser.initDataUser(){

                var locale = Locale(userData.language)
                val res = resources
                val dm = res.displayMetrics
                val conf = res.configuration
                conf.setLocale(locale)
                res.updateConfiguration(conf, dm)
                val homeActivity = Intent(this, HomeActivity::class.java)
                startActivity(homeActivity)
            }


        }else{
            val registerActivity = Intent(this, RegisterActivity::class.java)
            startActivity(registerActivity)
        }

    }
}