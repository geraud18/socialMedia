package com.iris.socialmedia.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.iris.socialmedia.R
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.UserModel
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.dataBaseReferenceUser
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth


class RegisterActivity : AppCompatActivity() {

    lateinit var helpers: Helpers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        val loginLink = findViewById<TextView>(R.id.register_login_link)
        val registerButtonSubmit = findViewById<Button>(R.id.register_submit_button)

        val emailField = findViewById<EditText>(R.id.register_email)
        val emailFieldError = findViewById<TextView>(R.id.register_email_error)

        val passwordField = findViewById<EditText>(R.id.register_password)
        val passwordFieldError = findViewById<TextView>(R.id.register_password_error)

        val confirmPasswordField = findViewById<EditText>(R.id.register_confirm_password)
        val confirmPasswordFieldError = findViewById<TextView>(R.id.register_confirm_password_error)

        registerButtonSubmit.isEnabled = false

        emailField.setOnFocusChangeListener { _, focused ->
            if(!focused){
                if(helpers.verifEmail(emailField)) {
                    emailField.setBackgroundResource(R.drawable.bg_rounded_input_success)
                    emailFieldError.visibility = View.GONE
                    registerButtonSubmit.setBackgroundResource(R.drawable.bg_rounded)
                    registerButtonSubmit.isEnabled = true
                }else{
                    emailField.setBackgroundResource(R.drawable.bg_rounded_input_error)
                    emailFieldError.visibility = View.VISIBLE
                    registerButtonSubmit.setBackgroundResource(R.drawable.bg_rounded_disabled)
                    registerButtonSubmit.isEnabled = false
                }
            }
        }

        passwordField.setOnFocusChangeListener { _, focused ->
            if(!focused){
                if(helpers.verifPassword(passwordField)) {
                    passwordField.setBackgroundResource(R.drawable.bg_rounded_input_success)
                    passwordFieldError.text = "Mot de passe fort"
                    passwordFieldError.setTextColor(Color.parseColor("#008000"))
                    passwordFieldError.visibility = View.VISIBLE
                    registerButtonSubmit.setBackgroundResource(R.drawable.bg_rounded)
                    registerButtonSubmit.isEnabled = true

                }else{
                    passwordField.setBackgroundResource(R.drawable.bg_rounded_input_error)
                    passwordFieldError.text = "Mot de passe faible"
                    passwordFieldError.setTextColor(Color.parseColor("#B22222"))
                    passwordFieldError.visibility = View.VISIBLE
                    registerButtonSubmit.setBackgroundResource(R.drawable.bg_rounded_disabled)
                    registerButtonSubmit.isEnabled = false
                }
            }
        }

        confirmPasswordField.setOnFocusChangeListener { _, focused ->
            if(!focused){
                if(helpers.verifPassword(confirmPasswordField)) {
                    confirmPasswordField.setBackgroundResource(R.drawable.bg_rounded_input_success)
                    confirmPasswordFieldError.text = "Mot de passe fort"
                    confirmPasswordFieldError.setTextColor(Color.parseColor("#008000"))
                    confirmPasswordFieldError.visibility = View.VISIBLE
                    registerButtonSubmit.setBackgroundResource(R.drawable.bg_rounded)
                    registerButtonSubmit.isEnabled = true

                }else{
                    confirmPasswordField.setBackgroundResource(R.drawable.bg_rounded_input_error)
                    confirmPasswordFieldError.text = "Mot de passe faible"
                    confirmPasswordFieldError.setTextColor(Color.parseColor("#B22222"))
                    confirmPasswordFieldError.visibility = View.VISIBLE
                    registerButtonSubmit.setBackgroundResource(R.drawable.bg_rounded_disabled)
                    registerButtonSubmit.isEnabled = false
                }
            }
        }

        loginLink.setOnClickListener {
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
        }

        registerButtonSubmit.setOnClickListener { registerUser() }

    }

    private fun registerUser() {

        val email = findViewById<EditText>(R.id.register_email).text.toString()
        val username = findViewById<EditText>(R.id.register_username).text.toString()
        val password = findViewById<TextInputEditText>(R.id.register_password).text.toString()
        val confirmPassword = findViewById<TextInputEditText>(R.id.register_confirm_password).text.toString()
        val confirmPasswordFieldError = findViewById<TextView>(R.id.register_confirm_password_error)


        if(email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){

            if(password == confirmPassword){

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val firebaseUser = FirebaseAuth.getInstance().currentUser
                        if (firebaseUser != null) {
                            val user = UserModel(
                                firebaseUser.uid,
                                "",
                                email,
                                "",
                                "",
                                username,
                                "",
                                "",
                                "",
                                ""
                            )
                            dataBaseReferenceUser.child(firebaseUser.uid).setValue(user)
                        }

                        val homeActivity = Intent(this, HomeActivity::class.java)
                        startActivity(homeActivity)

                    }else{
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                confirmPasswordFieldError.text = "Mot de passe incorrect"
                confirmPasswordFieldError.setTextColor(Color.parseColor("#B22222"))
                confirmPasswordFieldError.visibility = View.VISIBLE
            }
        }
        else{
            Toast.makeText(this,"Tous les champs ne sont pas remplis", Toast.LENGTH_SHORT).show()
        }

    }
}