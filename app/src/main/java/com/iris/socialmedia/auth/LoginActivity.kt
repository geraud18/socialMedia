package com.iris.socialmedia.auth

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.iris.socialmedia.R
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.model.UserModel
import com.iris.socialmedia.pages.HomeActivity
import com.iris.socialmedia.repository.UserRepository.Singleton.dataBaseReferenceUser
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    val helpers = Helpers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val registerLink = findViewById<TextView>(R.id.login_register_link)
        val btnLoginSubmit = findViewById<Button>(R.id.login_connexion_button)
        val btnLoginForgetPassword = findViewById<TextView>(R.id.login_forget_password)
        val btnLoginGoogle = findViewById<TextView>(R.id.login_connexion_google_link)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("278564623041-ntd0i4h6btvovt3keg86j4urh2480chi.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        registerLink.setOnClickListener {
            val registerActivity = Intent(this, RegisterActivity::class.java)
            startActivity(registerActivity)
            finish()
        }

        btnLoginForgetPassword.setOnClickListener {
            val forgetPasswordActivity = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(forgetPasswordActivity)
            finish()
        }

        btnLoginGoogle.setOnClickListener { loginGoogle() }

        btnLoginSubmit.setOnClickListener { loginUser() }
    }

    private fun loginGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        getResult.launch(signInIntent)
    }
    
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        }catch (e:ApiException){
            Log.w(TAG, getString(R.string.error_google_sign), e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {

        if (firebaseUser != null) {
            val user = UserModel(
                firebaseUser.uid,
                "fr",
                "",
                firebaseUser.email,
                "",
                "",
                firebaseUser.displayName,
                "",
                "",
                "",
                "",
                ""
            )
            dataBaseReferenceUser.child(firebaseUser.uid).setValue(user)
            helpers.startApplication(this){
                val homeActivity = Intent(this, HomeActivity::class.java)
                startActivity(homeActivity)
                finish()
            }
        }

    }

    private fun loginUser() {
        val loginError = findViewById<TextView>(R.id.login_page_error)
        val emailField = findViewById<EditText>(R.id.login_email).text.toString()
        val passwordField = findViewById<TextInputEditText>(R.id.login_password).text.toString()

        if(emailField.isNotEmpty() && passwordField.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(emailField, passwordField).addOnCompleteListener {
                    if(it.isSuccessful){
                        helpers.startApplication(this){
                            val homeActivity = Intent(this, HomeActivity::class.java)
                            startActivity(homeActivity)
                            finish()
                        }
                    }else{
                        loginError.text = getString(R.string.error_field_login_incorrect)
                        loginError.visibility = View.VISIBLE
                    }
                }
        }
        else{
            loginError.text = getString(R.string.error_field_empty)
            loginError.visibility = View.VISIBLE
        }


    }
}