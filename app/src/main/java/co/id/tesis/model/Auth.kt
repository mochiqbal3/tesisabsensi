package co.id.tesis.model

import com.gammasolution.segarmart.util.connection.ServiceInterface
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Auth{
    @SerializedName("success")
    var success = false
    @SerializedName("data")
    var data = Profile()
    companion object {
        val serviceInterface by lazy {
            ServiceInterface.create()
        }

        fun login(username:String,password:String,deviceId : String) : Observable<Auth> {

            return  serviceInterface.login(username,password,deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

    }
}

class Profile{
    @SerializedName("_id")
    var _id = ""
    @SerializedName("kelas_id")
    var kelas_id = ""
    @SerializedName("device_id")
    var device_id = ""
    @SerializedName("no_induk")
    var no_induk = ""
    @SerializedName("nama")
    var nama = ""
    @SerializedName("email")
    var email = ""
    @SerializedName("jk")
    var jk = ""
    @SerializedName("phone")
    var phone = ""
    @SerializedName("roles")
    var roles = ""
    @SerializedName("created_at")
    var created_at = ""
    @SerializedName("updated_at")
    var updated_at = ""
    @SerializedName("kelas")
    var kelas = Kelas()
}

class Kelas{
    @SerializedName("_id")
    var _id = ""
    @SerializedName("nama")
    var nama = ""
}