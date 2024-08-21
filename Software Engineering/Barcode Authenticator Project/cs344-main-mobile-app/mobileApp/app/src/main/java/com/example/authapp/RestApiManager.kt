package com.example.authapp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class RestApiManager {
    fun addUser(userData: UserInfo, onResult: (UserInfo?) -> Unit){
        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.addUser(userData).enqueue(
            object : Callback<UserInfo> {
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {

                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }
    fun sendQR(QRData: QRResponse, onResult: (QRResponse?) -> Unit){

        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.sendQR(QRData).enqueue(
            object : Callback<QRResponse> {
                override fun onFailure(call: Call<QRResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse( call: Call<QRResponse>, response: Response<QRResponse>) {

                    val addedQR = response.body()
                    onResult(addedQR)
                }
            }
        )
    }
    fun sendGUID(dataGUID: dataGUID, onResult: (dataGUID?) -> Unit){

        val retrofit = ServiceBuilder.buildService(RestApi::class.java)
        retrofit.sendGUID(dataGUID).enqueue(
            object : Callback<dataGUID> {
                override fun onFailure(call: Call<dataGUID>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse( call: Call<dataGUID>, response: Response<dataGUID>) {

                    val guiResponse = response.body()
                    onResult(guiResponse)
                }
            }
        )
    }
}