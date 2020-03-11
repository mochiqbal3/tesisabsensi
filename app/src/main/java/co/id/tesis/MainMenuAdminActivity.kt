package co.id.tesis

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.id.distriboost.util.Constant
import co.id.distriboost.util.SharedPref
import co.id.tesis.adapter.MacAddressAdapter
import co.id.tesis.model.Auth
import co.id.tesis.model.MyLocation
import co.id.tesis.model.ResponseMacAddress
import com.gammasolution.segarmart.util.connection.ServiceInterface
import kotlinx.android.synthetic.main.activity_main_menu_admin.*
import kotlinx.android.synthetic.main.fragment_rekap_absensi.*

class MainMenuAdminActivity : AppCompatActivity() {

    private val sharedPref: SharedPref by lazy {
        SharedPref(this, getString(R.string.key_auth))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu_admin)
        if (Constant.checkNetworkState(this))
            tvMacAddress.text = Constant.getSSID(this)
        else{
            Toast.makeText(this,"Pastikan terkoneksi ke wifi",Toast.LENGTH_SHORT).show()
        }
        if (checkPermission()) {
            MyLocation.getLocation(this, locationResult)
        } else {
            grantPermission()
        }
    }

    fun grantPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
            ), MainActivity.REQUEST_READ_PHONE_STATE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.REQUEST_READ_PHONE_STATE) {
            if (grantResults.size <= 0) {
//                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MyLocation.getLocation(this, locationResult)
            }
        }
    }

    fun checkPermission(): Boolean {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_WIFI_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
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
                    tvLatitude.text = latitude.toString()
                    tvLongitude.text = longitude.toString()
                } catch (e: Exception) { // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_admin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.logout) {
            sharedPref.setLogin(Auth(), "")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
