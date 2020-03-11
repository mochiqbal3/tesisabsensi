package co.id.distriboost.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*
import android.media.MediaDrm
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.util.*

class Constant{
    companion object{
        val grant_type = "password"
        val client_id = "2"
        val client_secret = "9kvcNAM7Mpba2hlhdAkaWWHcPlNXiS1feoPpy2QX"
        val DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var RANDOM = Random()

        fun randomString(len: Int): String {
            val sb = StringBuilder(len)

            for (i in 0 until len) {
                sb.append(DATA[RANDOM.nextInt(DATA.length)])
            }

            return sb.toString()
        }

        @JvmStatic
        fun dateToSimpleString(date: Date, format: SimpleDateFormat) : String {
            return format.format(date)
        }

        @JvmStatic
        fun stringToDate(string: String, format: SimpleDateFormat) : Date {
            return format.parse(string)
        }

        fun getMacAddr(): String? {
            try {
                val all: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (!nif.getName().equals("wlan0",true)) continue
                    val macBytes: ByteArray = nif.getHardwareAddress() ?: return ""
                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }
                    if (res1.length > 0) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ex: Exception) {
            }
            return "02:00:00:00:00:00"
        }

        fun getSSID(context: Context): String? {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
            val info = wifiManager!!.connectionInfo
            val ssid = info.bssid
            return ssid.toUpperCase()
        }

        fun checkNetworkState(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> false
                    else -> false
                }
            } else {
                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                return nwInfo.isConnected
            }
        }

//        fun placeHolderImage(context: Context) : CircularProgressDrawable{
//            val circularProgressDrawable = CircularProgressDrawable(context)
//            circularProgressDrawable.strokeWidth = 5f
//            circularProgressDrawable.centerRadius = 30f
//            circularProgressDrawable.start()
//            var drawable = context.getDrawable(R.drawable.placeholder)
//            return circularProgressDrawable
//        }
//
//        fun placeHolderCam(context: Context) : Drawable{
//            var drawable = context.getDrawable(R.drawable.placeholder)
//            return drawable!!
//        }
    }
}

object UniqueDeviceID {

    /**
     * UUID for the Widevine DRM scheme.
     * <p>
     * Widevine is supported on Android devices running Android 4.3 (API Level 18) and up.
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getUniqueId(): String? {

        val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
        var wvDrm: MediaDrm? = null
        try {
            wvDrm = MediaDrm(WIDEVINE_UUID)
            val widevineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            val md = MessageDigest.getInstance("SHA-256")
            md.update(widevineId)
            return  md.digest().toHexString()
        } catch (e: Exception) {
            //WIDEVINE is not available
            return null
        } finally {
            if (Build.VERSION.SDK_INT >= 28) {
                wvDrm?.close()
            } else {
                wvDrm?.release()
            }
        }
    }


    fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}