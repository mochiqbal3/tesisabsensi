package co.id.tesis.fragment

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.id.distriboost.util.Constant
import co.id.distriboost.util.SharedPref
import co.id.tesis.MainActivity
import co.id.tesis.PresensiActivity
import co.id.tesis.R
import co.id.tesis.model.*
import com.gammasolution.segarmart.util.connection.ServiceInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var tvNIM: TextView
    lateinit var tvNama: TextView
    lateinit var tvKelas: TextView
    lateinit var tvTanggal: TextView
    lateinit var tvMasuk: TextView
    lateinit var tvKeluar: TextView
    lateinit var tvAlert: TextView
    lateinit var alert: LinearLayout
    lateinit var fab: FloatingActionButton
    var presentToday = Presensi()
    val gson = Gson()
    var profile = Profile()
    var macAddress: String? = ""
    var isPulang = false
    var absenPulang = false

    var selectedRouter = Router()

    private val sharedPref: SharedPref by lazy {
        SharedPref(context!!, getString(R.string.key_auth))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        profile = sharedPref.getProfile()
        initUi(view)
        hidePresensi()
        hideAlert()
        getPresensiToday()

        return view
    }

    fun grantPermission() {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
            ), MainActivity.REQUEST_READ_PHONE_STATE
        )
    }

    fun checkPermission(): Boolean {

        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_WIFI_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun initUi(view: View) {
        tvNIM = view.tvNIM
        tvNama = view.tvNama
        tvKelas = view.tvKelas
        fab = view.fab
        tvTanggal = view.tvTanggal
        tvMasuk = view.tvMasuk
        tvKeluar = view.tvKeluar
        tvAlert = view.tvAlert
        alert = view.alert
        tvNIM.text = profile.no_induk
        tvNama.text = profile.nama
        tvKelas.text = profile.kelas.nama
        val date = Date()
        tvTanggal.text = Constant.dateToSimpleString(date, SimpleDateFormat("dd MMM yyyy"))
    }

    fun showMessageSuccess(message: String) {
        tvAlert.text = message
        tvAlert.setTextColor(Color.parseColor("#21dfcb"))
        alert.visibility = View.VISIBLE
    }

    fun getPresensiToday() {
        val progress = ProgressDialog(context)
        progress.setTitle(getString(R.string.string_loading))
        progress.setMessage(getString(R.string.message_loading))
        progress.setCancelable(false)
        progress.show()
        ResponsePresensi.getPresensiToday(sharedPref.getProfile().no_induk).subscribe(
            { success ->
                progress.dismiss()
                if (success.data.size > 0) {
                    presentToday = success.data[0]
                    if (presentToday != null) {
                        tvMasuk.text = presentToday.jam_masuk
                        tvKeluar.text = presentToday.jam_keluar
                        if (presentToday.jam_keluar == "-") {
                            Log.e("masuk", "atas")
                            isPulang = false
                            absenPulang = true
                        } else {
                            isPulang = true
                        }
                    }
                }
                if (Constant.checkNetworkState(context!!)) {
                    isValidConnection()
                } else {
                    showAlert("Silahkan terhubung ke jaringan wifi yang telah ditentukan.")
                }

            }, { error ->
                progress.dismiss()
                context?.let { ServiceInterface.handleError(error, it) }
            }
        )
    }



    private fun isValidConnection() {
        val progress = ProgressDialog(context)
        progress.setTitle(getString(R.string.string_loading))
        progress.setMessage(getString(R.string.message_loading))
        progress.setCancelable(false)
        progress.show()
        ResponseMacAddress.getMacAddress().subscribe(
            { success ->
                progress.dismiss()
                val wifiManager =
                    context!!.getSystemService(
                        Context.WIFI_SERVICE
                    ) as WifiManager
                val wInfo = wifiManager.connectionInfo
                macAddress = Constant.getSSID(activity!!.applicationContext)
                Log.e("macAddress", macAddress)
                if (!validateMacAddress(success.data, macAddress!!)) {
                    showAlert("Silahkan terhubung ke jaringan wifi yang telah ditentukan.")
                } else {
                    if (checkPermission()) {
                        MyLocation.getLocation(context!!, locationResult)
                    } else {
                        grantPermission()
                    }
                }
            }, { error ->
                progress.dismiss()
                context?.let { ServiceInterface.handleError(error, it) }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        getPresensiToday()
    }


    private fun validateMacAddress(data: MutableList<Router>, macAddress: String): Boolean {
        for (item in data) {
            if (item.mac_address == macAddress) {
                selectedRouter = item
                return true
            }
        }
        return false
    }

    fun isValidTime() {
        val date = Date()
        val hour = Constant.dateToSimpleString(date, SimpleDateFormat("HH"))
        Log.e("jam", hour)
        if ((hour.toInt() >= 6 && hour.toInt() < 9)) {
            showPresensi()
        } else if ((hour.toInt() >= 14 && hour.toInt() <= 23)) {
            Log.e("dataAbsen", gson.toJson(presentToday))
            if (presentToday.id == "") {
                showAlert("Anda tidak absen masuk hari ini")
            } else {
                if (isPulang) {
                    Log.e("masuk", "bawah")
                    hidePresensi()
                    showMessageSuccess("Anda sudah melakukan absensi hari ini")
                } else
                    showPresensi()
            }
        } else {
            if (isPulang) {
                Log.e("masuk", "bawah")
                hidePresensi()
                showMessageSuccess("Anda sudah melakukan absensi hari ini")
            } else {
                showAlert("Silahkan absen pada waktu yang telah ditentukan")
                hidePresensi()
            }
        }
    }

    fun showAlert(message: String) {
        tvAlert.text = message
        alert.visibility = View.VISIBLE
        tvAlert.setTextColor(Color.parseColor("#e7026e"))
    }

    fun hideAlert() {
        alert.visibility = View.GONE
    }

    fun hidePresensi() {
        fab.hide()
    }

    fun showPresensi() {
        fab.show()
        fab.setOnClickListener {
            val intent = Intent(context, PresensiActivity::class.java)
            intent.putExtra("isPulang", absenPulang)
            startActivity(intent)
        }
    }

    var locationResult: MyLocation.Companion.LocationResult =
        object : MyLocation.Companion.LocationResult() {
            override fun gotLocation(location: Location?) {
                val longitude: Double = location!!.getLongitude()
                val latitude: Double = location.getLatitude()
//                Toast.makeText(
//                    context, "Got Location",
//                    Toast.LENGTH_LONG
//                ).show()
                try {
                    val locNow = Location("")
                    locNow.latitude = latitude
                    locNow.longitude = longitude

                    val locRouter = Location("")
                    locRouter.latitude = selectedRouter.lat
                    locRouter.longitude = selectedRouter.lng

                    val distanceInMeters: Float = locNow.distanceTo(locRouter)
                    Log.e("jarak",distanceInMeters.toString())
                    if (distanceInMeters >= 0.0F && distanceInMeters <= 25.0F)
                        isValidTime()
                    else{
                        hidePresensi()
                        showAlert("Jarak anda saat ini melebihi 25 meter dari lokasi yang telah ditentukan")
                    }
                    Log.e("lokasi", latitude.toString() + " " + longitude.toString())
                } catch (e: Exception) { // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
