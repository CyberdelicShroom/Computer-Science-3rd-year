package com.example.authapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_logged_in.*

var code:String = ""

class QRCodeSent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_logged_in)

        userNameText.text = userInfo.username
        userEmailText.text = userInfo.email

    }
    fun onCLickSignOut(view: View) {

        userInfo.username = null
        userInfo.email = null
        userInfo.password = null
        userInfo.token = null
        userInfo.status = null
        userInfo.details = null

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}