package co.id.distriboost.util

import android.content.Context
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat.getSystemService
import java.net.NetworkInterface
import java.text.SimpleDateFormat
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
