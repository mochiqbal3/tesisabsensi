package co.id.tesis

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.id.distriboost.util.Constant
import co.id.distriboost.util.SharedPref
import co.id.tesis.model.Auth
import co.id.tesis.model.MyLocation
import com.gammasolution.segarmart.util.connection.ServiceInterface
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val sharedPref: SharedPref by lazy {
        SharedPref(applicationContext!!, getString(R.string.key_auth))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("macAddress", Constant.getMacAddr())
        Log.e("macAddress", Constant.getSSID(applicationContext))

        initEvent()
    }



    private fun initEvent() {
        btnLogin.setOnClickListener {
            if (validation()) {
                if (checkPermission())
                    login()
                else grantPermission()
            }
        }
    }

    private fun validation(): Boolean {
        if (etUsername.text.toString().isEmpty()) {
            etUsername.error = "Silahkan isi username"
            return false
        }
        if (etPassword.text.toString().isEmpty()) {
            etPassword.error = "Silahkan isi password"
            return false
        }
        return true
    }

    fun grantPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity, arrayOf(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
            ), REQUEST_READ_PHONE_STATE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.size <= 0) {
//                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                MyLocation.getLocation(this,locationResult)
                login()
            }
        }
    }

    private fun showSnackbar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {

        Toast.makeText(this, getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }

    fun checkPermission(): Boolean {

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_WIFI_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun login() {
        val progress = ProgressDialog(this)
        progress.setTitle(getString(R.string.string_loading))
        progress.setMessage(getString(R.string.message_loading))
        progress.setCancelable(false)
        progress.show()
        val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var imei = ""
        if (checkPermission()) {
            imei = telephonyManager.deviceId
        }
        Auth.login(etUsername.text.toString(), etPassword.text.toString(), imei).subscribe(
            { success ->
                progress.dismiss()
                sharedPref.setLogin(success, etUsername.text.toString())
                val intent = Intent(this, MainMenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }, { error ->
                progress.dismiss()
                ServiceInterface.handleError(error, this)
            }
        )
    }

    companion object {
        val REQUEST_READ_PHONE_STATE = 1001
    }
}
