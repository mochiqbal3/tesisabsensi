package co.id.tesis.model

import com.gammasolution.segarmart.model.BasicErrorModel
import com.gammasolution.segarmart.util.connection.ServiceInterface
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ResponsePresensi{
    @SerializedName("success")
    var success = false
    @SerializedName("data")
    var data = mutableListOf<Presensi>()
    companion object {
        val serviceInterface by lazy {
            ServiceInterface.create()
        }

        fun getPresensiToday(no_induk:String) : Observable<ResponsePresensi> {

            return  serviceInterface.getPresentToday(no_induk)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

        fun getPresensiRange(no_induk:String,
                             from : String,
                             to : String) : Observable<ResponsePresensi> {

            return  serviceInterface.getPresentRange(no_induk,from,to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

        fun presentToday(nip: String,
                         mac_address: String,
                         tanggal: String,
                         lat: Double,
                         lng: Double) : Observable<BasicErrorModel> {

            return  serviceInterface.postPresent(nip,
                    mac_address,
                    tanggal,
                    lat,
                    lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

    }
}

class Presensi{
    @SerializedName("_id")
    var id = ""
    @SerializedName("mac_address")
    var mac_address = ""
    @SerializedName("no_induk")
    var no_induk = ""
    @SerializedName("tanggal")
    var tanggal = ""
    @SerializedName("jam_masuk")
    var jam_masuk = ""
    @SerializedName("jam_keluar")
    var jam_keluar = ""
    @SerializedName("lat")
    var lat = 0.0
    @SerializedName("lng")
    var lng = 0.0
    @SerializedName("updated_at")
    var updated_at = ""
    @SerializedName("created_at")
    var created_at = ""
    @SerializedName("users")
    var users = Profile()
    @SerializedName("router")
    var router = Router()
}

class ResponseMacAddress{
    @SerializedName("success")
    var success = false
    @SerializedName("data")
    var data = mutableListOf<Router>()
    companion object {
        val serviceInterface by lazy {
            ServiceInterface.create()
        }

        fun getMacAddress() : Observable<ResponseMacAddress> {

            return  serviceInterface.getMacAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

    }
}

class Router{
    @SerializedName("_id")
    var _id = ""
    @SerializedName("mac_address")
    var mac_address = ""
    @SerializedName("nama")
    var nama = ""
    @SerializedName("lat")
    var lat :Double = 0.0
    @SerializedName("lng")
    var lng :Double= 0.0
    @SerializedName("created_at")
    var created_at = ""
    @SerializedName("updated_at")
    var updated_at = ""
}