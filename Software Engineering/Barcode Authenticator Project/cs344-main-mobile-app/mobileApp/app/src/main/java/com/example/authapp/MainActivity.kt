package com.example.authapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

var userInfo = UserInfo(
    email = null,
    password = null,
    details = null,
    status = null,
    token = null,
    username = null
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onCLick(view: View) {

        val email : String = findViewById<EditText>(R.id.textEmail).text.toString();
        val password : String = findViewById<EditText>(R.id.textPassword).text.toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_LONG).show()

        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_LONG).show()

        } else {
            Thread(Runnable {
                signIn(email, password)
            }).start()
        }
    }

    fun signIn(email: String, password: String) {
        val apiService = RestApiManager()
        val intent = Intent(this,LoggedIn::class.java)

        userInfo.email = email
        userInfo.password = password

        apiService.addUser(userInfo) {
            if (it?.username != null) {

                userInfo.username = it?.username
                userInfo.token = it?.token
                userInfo.status = it?.status

            } else {
                userInfo.details = it?.details
            }
        }
        Thread.sleep(500)

        if (userInfo.status == "success") {
            startActivity(intent)
        } else if (userInfo.details == "User does not exist") {
            this@MainActivity.runOnUiThread(java.lang.Runnable {
                Toast.makeText(this, "User does not exist!", Toast.LENGTH_LONG).show()
            })
        } else if (userInfo.details == "Password incorrect") {
            this@MainActivity.runOnUiThread(java.lang.Runnable {
                Toast.makeText(this, "Incorrect password!", Toast.LENGTH_LONG).show()
            })
        } else {
            this@MainActivity.runOnUiThread(java.lang.Runnable {
                Toast.makeText(this, "Unknown Error!", Toast.LENGTH_LONG).show()
            })
        }

    }
}