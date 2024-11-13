package com.lumko.teachme.model

import com.google.gson.annotations.SerializedName

class ForgetPassword {
    @SerializedName("email")
    lateinit var email: String
}