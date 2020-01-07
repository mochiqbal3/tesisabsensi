package com.gammasolution.segarmart.model

import com.google.gson.annotations.SerializedName

class BasicErrorModel {
    @SerializedName("success")
    var success = false
    @SerializedName("message")
    var message = ""
}
