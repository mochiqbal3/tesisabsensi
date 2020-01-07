package co.id.tesis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import co.id.tesis.fragment.HomeFragment
import co.id.tesis.fragment.ProfileFragment
import co.id.tesis.fragment.RekapAbsensiFragment
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        initEvent()
        loadFragment(HomeFragment.newInstance("",""),"")
    }

    fun initEvent(){
        navigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_absen -> {
                    loadFragment(HomeFragment.newInstance("", ""), "")
                }
                R.id.menu_rekap-> {
                    loadFragment(RekapAbsensiFragment.newInstance("", ""), "rekapAbsen")
                }
                R.id.menu_profil -> {
                    loadFragment(ProfileFragment.newInstance("", ""), "menuTransaksi")
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

    fun loadFragment(fragment: Fragment, backstack: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragmentContainer, fragment)

        if (backstack != "") {
            fragmentTransaction.addToBackStack(backstack)
        }
        fragmentTransaction.commit()
    }
}
