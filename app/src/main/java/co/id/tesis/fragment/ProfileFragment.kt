package co.id.tesis.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import co.id.distriboost.util.SharedPref
import co.id.tesis.MainActivity

import co.id.tesis.R
import co.id.tesis.model.Auth
import co.id.tesis.model.Profile
import kotlinx.android.synthetic.main.fragment_profile.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val sharedPref: SharedPref by lazy {
        SharedPref(context!!, getString(R.string.key_auth))
    }
    var profile = Profile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var tvNIM: TextView
    lateinit var tvNama: TextView
    lateinit var tvJK: TextView
    lateinit var tvKelas: TextView
    lateinit var tvEmail: TextView
    lateinit var tvPhone: TextView
    lateinit var btnLogout : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profile = sharedPref.getProfile()
        initUi(view)
        initEvent()
        return view
    }

    fun initUi(view: View){
        tvNIM = view.tvNIM
        tvNama = view.tvNama
        tvJK = view.tvJK
        tvKelas = view.tvKelas
        tvEmail = view.tvEmail
        tvPhone = view.tvPhone
        tvNIM.text = profile.no_induk
        tvNama.text = profile.nama
        var jenisKelamin = ""
        if (profile.jk=="L"){
            jenisKelamin = "Laki-Laki"
        }else{
            jenisKelamin = "Perempuan"
        }
        tvJK.text =  jenisKelamin
        tvKelas.text = profile.kelas.nama
        tvEmail.text = profile.email
        tvPhone.text = profile.phone
        btnLogout = view.btnLogout
    }

    fun initEvent(){
        btnLogout.setOnClickListener {
            sharedPref.setLogin(Auth(),"")
            val intent = Intent(context,MainActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
