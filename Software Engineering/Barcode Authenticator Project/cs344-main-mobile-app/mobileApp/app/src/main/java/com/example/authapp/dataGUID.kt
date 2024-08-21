package com.example.authapp

import com.google.gson.annotations.SerializedName

class dataGUID(
    @SerializedName("email") var email: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("uuid") var uuid: String?
)