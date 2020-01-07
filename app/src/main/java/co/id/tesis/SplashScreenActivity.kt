package co.id.tesis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.id.distriboost.util.SharedPref

class SplashScreenActivity : AppCompatActivity() {

    private val sharedPref: SharedPref by lazy {
        SharedPref(applicationContext!!, getString(R.string.key_auth))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sharedPref.getBoolean(getString(R.string.key_is_login),false)){
            val intent = Intent(this,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
