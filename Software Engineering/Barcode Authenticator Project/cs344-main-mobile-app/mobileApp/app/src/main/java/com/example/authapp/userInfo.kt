package com.example.authapp

import com.google.gson.annotations.SerializedName

class UserInfo(
    @SerializedName("email") var email: String?,
    @SerializedName("password") var password: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("token") var token: String?,
    @SerializedName("details") var details: String?,
    @SerializedName("username") var username: String?
)
