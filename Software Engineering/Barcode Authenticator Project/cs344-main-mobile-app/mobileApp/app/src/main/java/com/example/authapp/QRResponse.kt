package com.example.authapp

import com.google.gson.annotations.SerializedName

class QRResponse(
    @SerializedName("status") var status: String?,
    @SerializedName("qr") var qr: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("uuid") var uuid: String?
)
