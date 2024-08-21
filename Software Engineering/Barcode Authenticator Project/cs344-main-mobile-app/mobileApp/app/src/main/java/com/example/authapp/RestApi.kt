package com.example.authapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface RestApi {

    @Headers("Content-Type: application/json")
    @POST("/api/user/login")
    fun addUser(@Body userData: UserInfo): Call<UserInfo>

    @Headers("Content-Type: application/json")
    @POST("/api/user/handleVerifyQR")
    fun sendQR(@Body QRData: QRResponse): Call<QRResponse>

    @Headers("Content-Type: application/json")
    @PUT("/api/user/updateUserUuid")
    fun sendGUID(@Body dataGUID: dataGUID): Call<dataGUID>
}