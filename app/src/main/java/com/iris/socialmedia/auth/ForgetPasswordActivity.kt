package com.iris.socialmedia.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_forget_password)

        firebaseAuth = FirebaseAuth.getInstance()

        val btnSubmitForgetPassword = findViewById<Button>(R.id.forget_reset_password_button)
        val forgetPasswordErrorReturnLogin = findViewById<Button>(R.id.forget_reset_password_return_login)

        btnSubmitForgetPassword.setOnClickListener { forgetPasswordUser() }

        forgetPasswordErrorReturnLogin.setOnClickListener {
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
            finish()
        }
    }

    private fun forgetPasswordUser() {

        val emailField = findViewById<EditText>(R.id.forget_passord_email).text.toString()
        val forgetPasswordError = findViewById<TextView>(R.id.forget_page_error)
        val btnSubmitForgetPassword = findViewById<Button>(R.id.forget_reset_password_button)
        val forgetPasswordErrorReturnLogin = findViewById<Button>(R.id.forget_reset_password_return_login)

        if(emailField.isEmpty()){
            forgetPasswordError.text = getString(R.string.error_field_required)
            forgetPasswordError.visibility = View.VISIBLE
        }else{
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailField).addOnCompleteListener {
                if(it.isSuccessful){
                    forgetPasswordError.text = getString(R.string.notifie_forget_password)
                    forgetPasswordError.setTextColor(Color.parseColor("#008000"))
                    forgetPasswordError.visibility = View.VISIBLE
                    btnSubmitForgetPassword.visibility = View.GONE
                    forgetPasswordErrorReturnLogin.visibility = View.VISIBLE
                }
            }
        }
    }
}