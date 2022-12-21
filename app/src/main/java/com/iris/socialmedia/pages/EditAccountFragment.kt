package com.iris.socialmedia.pages

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.methodes.Helpers
import com.iris.socialmedia.repository.UserRepository
import com.iris.socialmedia.repository.UserRepository.Singleton.downloadlProfileImage
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.userData
import java.util.*


class EditAccountFragment(
    private val context: HomeActivity
) : Fragment() {

    private var uploadedImage: ImageView? = null
    private var file: Uri? = null
    lateinit var locale: Locale
    private var currentLanguage = userData.language
    val helpers = Helpers()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()
        val viewEditAccount = inflater.inflate(R.layout.fragment_edit_account, container, false)

        showValueUser(viewEditAccount)

        val toolBarEditAccount = viewEditAccount.findViewById<Toolbar>(R.id.top_bar_edit_account)
        val btnUpdateEditAccount = viewEditAccount.findViewById<Button>(R.id.edit_account_btn_update)

        toolBarEditAccount.inflateMenu(R.menu.languages_menu)

        toolBarEditAccount.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.french -> {
                    setLocale("fr")
                    true
                }
                R.id.english -> {
                    setLocale("en")
                    true
                }
                else -> false
            }
        }

        toolBarEditAccount.setNavigationIcon(R.drawable.custumleftarrow)

        //  context.supportActionBar!!.hide()
        //  context.setSupportActionBar(toolBarEditAccount)
        //  context.supportActionBar!!.title = getString(R.string.edit_account_title)
        //  context.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolBarEditAccount.setNavigationOnClickListener {
            context.onBackPressed()
        }

        uploadedImage = viewEditAccount.findViewById(R.id.edit_account_image)

        val fieldDate = viewEditAccount.findViewById<EditText>(R.id.edit_account_birthday)
        val editImageAccount = viewEditAccount.findViewById<ImageView>(R.id.btn_edit_account_image)

        editImageAccount?.setOnClickListener { pickupImage() }

        fieldDate.setOnClickListener { datePicker(fieldDate) }

        btnUpdateEditAccount.setOnClickListener { saveFormDatabase(viewEditAccount) }

        return viewEditAccount
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
                context,
                HomeActivity::class.java
            )
            userData.language = localeName
            val repoUser = UserRepository()
            repoUser.updateUser(userData)
            repoUser.initDataUser(){
                startActivity(refresh)
                Toast.makeText(context,getString(R.string.notifie_account_update_language), Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(context, getString(R.string.notifie_account_select_language), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showValueUser(viewEditAccount: View?) {
        // show data user in update form
        val profileImage = viewEditAccount?.findViewById<ImageView>(R.id.edit_account_image)
        if(userData.profile?.isNotEmpty() == true){

            if (profileImage != null) {
                Glide.with(this).load(Uri.parse(userData.profile)).into(profileImage)
                helpers.updateCircleImage(profileImage)
            }
        }

        viewEditAccount?.findViewById<EditText>(R.id.edit_account_birthday)?.setText(userData.birthday)

        viewEditAccount?.findViewById<EditText>(R.id.edit_account_name)?.setText(userData.name)

        viewEditAccount?.findViewById<EditText>(R.id.edit_account_firstname)?.setText(userData.firstname)

        viewEditAccount?.findViewById<EditText>(R.id.edit_account_bio_information)?.setText(userData.bioInformation)

        viewEditAccount?.findViewById<EditText>(R.id.edit_account_link_linkedin)?.setText(userData.linkedIn)

        val selectItemStatut =  viewEditAccount?.findViewById<Spinner>(R.id.edit_account_statut)

        if(userData.statut == getString(R.string.edit_account_statut_man)){
            selectItemStatut?.setSelection(1)
        }else if (userData.statut == getString(R.string.edit_account_statut_woman)){
            selectItemStatut?.setSelection(2)
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveFormDatabase(viewEditAccount: View?) {

        var btnUpdate = viewEditAccount?.findViewById<Button>(R.id.edit_account_btn_update)
        var progressBar = viewEditAccount?.findViewById<FrameLayout>(R.id.progressbar_update)
        btnUpdate?.visibility = View.GONE
        progressBar?.visibility = View.VISIBLE

        val repoUser = UserRepository()
        if(file != null){
            repoUser.uploadImageProfile(file!!){
                userData.profile = downloadlProfileImage.toString()
                valueUpdate(viewEditAccount)
                repoUser.updateUser(userData)

                btnUpdate?.visibility = View.VISIBLE
                progressBar?.visibility = View.GONE
                refreshValue()
            }
        }else{

            valueUpdate(viewEditAccount)
            repoUser.updateUser(userData)

            btnUpdate?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
            refreshValue()
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


    private fun valueUpdate(viewEditAccount: View?){
        userData.birthday = viewEditAccount?.findViewById<EditText>(R.id.edit_account_birthday)?.text.toString()
        userData.name= viewEditAccount?.findViewById<EditText>(R.id.edit_account_name)?.text.toString()
        userData.firstname = viewEditAccount?.findViewById<EditText>(R.id.edit_account_firstname)?.text.toString()
        userData.statut = viewEditAccount?.findViewById<Spinner>(R.id.edit_account_statut)?.selectedItem.toString()
        userData.linkedIn = viewEditAccount?.findViewById<EditText>(R.id.edit_account_link_linkedin)?.text.toString()
        userData.bioInformation = viewEditAccount?.findViewById<EditText>(R.id.edit_account_bio_information)?.text.toString()
    }


    private fun refreshValue(){

        val repoUser = UserRepository()
        repoUser.initDataUser(){
            Toast.makeText(context,getString(R.string.notifie_success_update_profil), Toast.LENGTH_SHORT).show()
        }

    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.GetContent() ) {

            if( it != null) {
                file = it
                uploadedImage?.setImageURI(file)

                helpers.updateCircleImage(uploadedImage!!)

            } else{
                return@registerForActivityResult
            }

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
            context,
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