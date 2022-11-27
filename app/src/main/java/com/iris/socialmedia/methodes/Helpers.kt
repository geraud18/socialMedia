package com.iris.socialmedia.methodes

import android.widget.EditText
import java.util.regex.Matcher
import java.util.regex.Pattern

class Helpers {

    fun verifEmail(emailParams: EditText) : Boolean {

        val regex = "^[A-Za-z0-9+_.-]+@(.+)\$"
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(emailParams.text.toString())
        return matcher.matches()
    }

    fun verifPassword(passwordParams:EditText) : Boolean{

        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(passwordParams.text.toString())

        return matcher.matches()
    }
}