package com.iris.socialmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.auth.LoginActivity
import com.iris.socialmedia.auth.RegisterActivity
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.methodes.LoadingDialog
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.repository.ContactRepository
import com.iris.socialmedia.repository.PublicationRepository
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.userData
import java.util.*

class MainActivity : AppCompatActivity() {

    val helpers = Helpers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startFirstPage()
    }

    override fun onResume() {
        super.onResume()
        finish()
    }

    private fun startFirstPage() {
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if(currentUser != null){
            helpers.startApplication(this){
                val homeActivity = Intent(this, HomeActivity::class.java)
                startActivity(homeActivity)
            }
        }else{
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
        }
    }


}