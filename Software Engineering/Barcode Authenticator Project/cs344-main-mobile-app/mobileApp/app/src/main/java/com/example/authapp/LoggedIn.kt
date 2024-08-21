package com.example.authapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_logged_in.*
import java.io.*

import java.util.*

var dataGuid = dataGUID (
    status = null,
    email = null,
    uuid = null,
)

class LoggedIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)

        userNameText.text = userInfo.username
        userEmailText.text = userInfo.email

        qrCodeButton.setEnabled(false);

        var files: Array<String> = fileList()

        if (files.contains("guid")) {
            qrCodeButton.setEnabled(true);
            trustedDeviceButton.setVisibility(View.GONE)
        }
    }

    fun onCLick(view: View) {
        val intent = Intent(this, Scanner::class.java)
        startActivity(intent)

    }

    fun onCLickSignOut(view: View) {

        userInfo.username = null
        userInfo.email = null
        userInfo.password = null
        userInfo.token = null
        userInfo.status = null
        userInfo.details = null

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

    fun onCLickTrusted(view: View) {

        var uniqueID = UUID.randomUUID().toString()

        val file:String = "guid"
        val data:String = uniqueID
        val fileOutputStream: FileOutputStream
        try {
            sendGUID(uniqueID)
            fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
            Toast.makeText(this, "Trusted device added", Toast.LENGTH_LONG).show()
            trustedDeviceButton.setVisibility(View.GONE)
            qrCodeButton.setEnabled(true);
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun sendGUID(guid: String) {
        val apiService = RestApiManager()
        dataGuid.uuid = guid
        dataGuid.email = userInfo.email

        apiService.sendGUID(dataGuid) {
            if (it?.status != null) {
                dataGuid.status = it?.status
            }
        }
        Thread.sleep(500)
    }

    override fun onBackPressed() {
    }
}
