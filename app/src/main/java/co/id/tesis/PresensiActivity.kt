package co.id.tesis

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import co.id.distriboost.util.Constant
import co.id.distriboost.util.SharedPref
import co.id.tesis.model.ResponsePresensi
import com.gammasolution.segarmart.util.connection.ServiceInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_presensi.*
import java.text.SimpleDateFormat
import java.util.*

class PresensiActivity : AppCompatActivity() {

    private val sharedPref: SharedPref by lazy {
        SharedPref(applicationContext!!, getString(R.string.key_auth))
    }

    var isPulang = false
    var macAddress : String? = ""
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presensi)
        if (!checkPermissions()) {
            requestPermissions()
        }else{
            getLastLocation()
        }
        initValue()
        initEvent()
    }

    private fun initValue() {
        isPulang = intent.getBooleanExtra("isPulang", false)
        if (isPulang) {
            btnPresensi.text = "Pulang"
        }
        tvNIM.text = sharedPref.getProfile().no_induk
        tvNama.text = sharedPref.getProfile().nama
        val date = Date()
        tvTanggal.text = Constant.dateToSimpleString(date, SimpleDateFormat("dd MMM yyyy"))
        tvWaktu.text = Constant.dateToSimpleString(date, SimpleDateFormat("HH:mm:ss"))
        macAddress = Constant.getMacAddr()
    }

    private fun initEvent() {
        btnPresensi.setOnClickListener {
            if (mLastLocation == null){
                Log.e("lokasi","kosong")
                if (!checkPermissions()) {
                    requestPermissions()
                }
            }else{
                present()
            }
        }
    }

    private fun present(){
        val date = Date()
        val progress = ProgressDialog(this)
        progress.setTitle(getString(R.string.string_loading))
        progress.setMessage(getString(R.string.message_loading))
        progress.setCancelable(false)
        progress.show()
        ResponsePresensi.presentToday(sharedPref.getProfile().no_induk,
            macAddress!!,
            Constant.dateToSimpleString(date, SimpleDateFormat("yyyy-MM-dd HH:mm:ss")),
            mLastLocation!!.latitude,
            mLastLocation!!.longitude
            ).subscribe(
            { success ->
                progress.dismiss()
                if (success.success){
                    Toast.makeText(this,"Absensi berhasil dilakukan",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this,"Silahkan coba absen kembali",Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                progress.dismiss()
                ServiceInterface.handleError(error, this)
            }
        )
    }

    private fun checkPermissions(): Boolean {
        val permissionState =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

        return permissionState == PackageManager.PERMISSION_GRANTED
    }



    private fun requestPermissions() {
            val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

        if (shouldProvideRationale!!) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.string_permission, android.R.string.ok,
                View.OnClickListener {
                    startLocationPermissionRequest()
                })

        } else {
//            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i("log", "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
//                Log.i(TAG, "User interaction was cancelled.")
            } else {
                showSnackbar(R.string.string_permission, R.string.string_settings,
                    View.OnClickListener {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    private fun startLocationPermissionRequest() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )

    }

    private fun showSnackbar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {

        Toast.makeText(this, getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }

    private fun getLastLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result
                } else {
                    Log.w("log", "getLastLocation:exception", task.exception)
                    Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object{
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

}
