package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.downloadlProfileImage
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.userData
import java.util.*


class EditAccountActivity : AppCompatActivity() {

    private var uploadedImage:ImageView? = null
    private var file: Uri? = null

    lateinit var locale: Locale
    private var currentLanguage = "fr"
    private var currentLang: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        firebaseAuth = FirebaseAuth.getInstance()

        showValueUser()

        val toolBarEditAccount = findViewById<Toolbar>(R.id.top_bar_edit_account)
        val btnUpdateEditAccount = findViewById<Button>(R.id.edit_account_btn_update)

        setSupportActionBar(toolBarEditAccount)
        supportActionBar!!.title = "Modifier profil"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolBarEditAccount.setNavigationOnClickListener {
            finish()
        }

       uploadedImage = findViewById(R.id.edit_account_image)

       val fieldDate = findViewById<EditText>(R.id.edit_account_birthday)
       val editImageAccount = findViewById<ImageView>(R.id.btn_edit_account_image)

       editImageAccount?.setOnClickListener { pickupImage() }

       fieldDate.setOnClickListener { datePicker(fieldDate) }

       btnUpdateEditAccount.setOnClickListener { saveFormDatabase() }

    }

    private fun showValueUser() {
        // AFFICHER LES IMFORMATIONS DE L'UTILISATEUR
        val profileImage = findViewById<ImageView>(R.id.edit_account_image)
        if(userData.profile?.isNotEmpty() == true){

            Glide.with(this).load(Uri.parse(userData.profile)).into(profileImage)
            updateCircleImage(profileImage)
        }

        findViewById<EditText>(R.id.edit_account_birthday).setText(userData.birthday)

        findViewById<EditText>(R.id.edit_account_name).setText(userData.name)

        findViewById<EditText>(R.id.edit_account_firstname).setText(userData.firstname)

        findViewById<EditText>(R.id.edit_account_bio_information).setText(userData.bioInformation)

        findViewById<EditText>(R.id.edit_account_link_linkedin).setText(userData.linkedIn)

        val selectItemStatut =  findViewById<Spinner>(R.id.edit_account_statut)

        if(userData.statut == "Monsieur"){
            selectItemStatut.setSelection(1)
        }else if (userData.statut == "Madame"){
            selectItemStatut.setSelection(2)
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveFormDatabase() {

        val repoUser = UserRepository()

        userData.birthday = findViewById<EditText>(R.id.edit_account_birthday).text.toString()
        userData.name= findViewById<EditText>(R.id.edit_account_name).text.toString()
        userData.firstname = findViewById<EditText>(R.id.edit_account_firstname).text.toString()
        userData.statut =findViewById<Spinner>(R.id.edit_account_statut).selectedItem.toString()
        userData.linkedIn = findViewById<EditText>(R.id.edit_account_link_linkedin).text.toString()
        userData.bioInformation = findViewById<EditText>(R.id.edit_account_bio_information).text.toString()

            if(file != null){
                repoUser.uploadImageProfile(file!!){
                    userData.profile = downloadlProfileImage.toString()
                }
            }

        repoUser.updateUser(userData)
        repoUser.initDataUser(){
            finish()
            Toast.makeText(this,"Le profil a ete modifier", Toast.LENGTH_SHORT).show()
        }

        //FAIRE APPARAITRE UNE MODAL VEUILLEZ PATIENTER

       /* val loading = LoadingDialog(this)
        loading.startLoading()
        var handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run(){
               loading.isDismiss()
            }
        },4000)*/

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.languages_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.french -> setLocale("fr")
            R.id.english -> setLocale("en")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.setLocale(locale)
            res.updateConfiguration(conf, dm)
            val refresh = Intent(
                this,
                HomeActivity::class.java
            )
            userData.language = localeName
            val repoUser = UserRepository()
            repoUser.updateUser(userData)
            repoUser.initDataUser(){
                startActivity(refresh)
                Toast.makeText(this, "Language changer", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Language, , already, , selected)!", Toast.LENGTH_SHORT).show()
        }
    }


    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.GetContent() ) {

        if( it != null) {
           file = it
           uploadedImage?.setImageURI(file)

           updateCircleImage(uploadedImage!!)

        } else{
           return@registerForActivityResult
        }

    }

    private fun updateCircleImage(imageProfile: ImageView) {

        imageProfile.layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        imageProfile.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;

        val marginParams = imageProfile.layoutParams as MarginLayoutParams
        marginParams.setMargins(0, 0, 0, 0)

        imageProfile.scaleType = ImageView.ScaleType.CENTER_CROP;
    }

    private fun pickupImage() {
        getResult.launch("image/*")
    }

    private fun datePicker(dateField: EditText) {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->

                val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                dateField.setText(dat)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}