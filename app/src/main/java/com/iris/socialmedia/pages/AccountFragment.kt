package com.iris.socialmedia.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.iris.socialmedia.R
import com.iris.socialmedia.repository.UserRepository.Singleton.firebaseAuth
import com.iris.socialmedia.repository.UserRepository.Singleton.userData


class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = FirebaseAuth.getInstance()

        val viewFragmentAccount = inflater.inflate(R.layout.fragment_account, container, false)

        val btnGetEditAccount = viewFragmentAccount.findViewById<TextView>(R.id.account_btn_modifier_profil)

        btnGetEditAccount.setOnClickListener {

            val intent = Intent(activity, EditAccountActivity::class.java)
            startActivity(intent)
        }

        showValueUser(viewFragmentAccount)

        val swipeRefreshLayout = viewFragmentAccount?.findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val refreshNameUser = viewFragmentAccount?.findViewById<TextView>(R.id.account_username)

        swipeRefreshLayout?.setOnRefreshListener{
            refreshNameUser?.text = refreshName()
            swipeRefreshLayout.isRefreshing = false
        }

        return viewFragmentAccount
    }

    private fun showValueUser(viewFragmentAccount: View?) {

        if(userData.name == "" && userData.firstname == ""){
            viewFragmentAccount?.findViewById<TextView>(R.id.account_username)?.text = "${userData.username}"
        }
        else{
            viewFragmentAccount?.findViewById<TextView>(R.id.account_username)?.text = "${userData.name}  ${userData.firstname}"
        }
    }

    fun refreshName() : String{
        if(userData.name == "" && userData.firstname == ""){
            return "${userData.username}"
        }
        else{
            return "${userData.name}  ${userData.firstname}"
        }
    }

}