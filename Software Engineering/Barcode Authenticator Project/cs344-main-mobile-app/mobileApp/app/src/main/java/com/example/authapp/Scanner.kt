package com.example.authapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

private const val CAMERA_REQUEST = 101

var qrResponse = QRResponse (
    status = null,
    qr = null,
    email = null,
    uuid = null
        )

class Scanner : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_scanner)

        setPermissions()
        startScanner()

    }

    private fun startScanner() {
        codeScanner = CodeScanner(this, findViewById<CodeScannerView>(R.id.CodeScanner_id))
        val intent = Intent(this, QRCodeSent::class.java)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            codeScanner.decodeCallback = DecodeCallback{

                // write result to scanner label
                code = it.text
                sendQR(code)
            }

            codeScanner.errorCallback= ErrorCallback{
                runOnUiThread{
                    Log.e("Main", "Camera init error: ${it.message}")
                }
            }
        }

        findViewById<CodeScannerView>(R.id.CodeScanner_id).setOnClickListener{
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

    //set up runtime permissions
    private fun setPermissions(){
        //get permission for camera
        val permission = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.CAMERA)

        if (permission!= PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this , arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST)
    }

    //this funct gives result of above request
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST->{
                if (grantResults.isEmpty() || grantResults[0]!= PackageManager.PERMISSION_GRANTED ){
                    Toast.makeText(this,"You need to grant camera permission for scanning to work",Toast.LENGTH_SHORT).show()
                }else{
                    //successful
                }
            }
        }

    }
    fun sendQR(qr: String) {
        val apiService = RestApiManager()
        val intent = Intent(this, QRCodeSent::class.java)

        var fis:FileInputStream? = openFileInput("guid")
        var isr: InputStreamReader = InputStreamReader(fis)
        val br: BufferedReader = BufferedReader(isr)
        val uuid = br.readLine()


        qrResponse.qr = qr
        qrResponse.email = userInfo.email
        qrResponse.uuid = uuid

        apiService.sendQR(qrResponse) {
            if (it?.status != null) {
                qrResponse.status = it?.status
            } else {

            }
        }
        Thread.sleep(500)

        if (qrResponse.status == "success") {
            startActivity(intent)
        } else {
            val intent = Intent(this, LoggedIn::class.java)
            startActivity(intent)
            Toast.makeText(this,"Something went wrong, try again",Toast.LENGTH_SHORT).show()
        }
    }
}