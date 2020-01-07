package co.id.tesis.fragment

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.id.distriboost.util.Constant
import co.id.distriboost.util.SharedPref

import co.id.tesis.R
import co.id.tesis.adapter.RekapAbsensiAdapter
import co.id.tesis.model.ResponsePresensi
import com.gammasolution.segarmart.util.connection.ServiceInterface
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_rekap_absensi.view.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RekapAbsensiFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RekapAbsensiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RekapAbsensiFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerView: RecyclerView
    lateinit var etFrom: TextInputEditText
    lateinit var etTo: TextInputEditText
    lateinit var btnCari: Button
    lateinit var tvPlaceholder : TextView

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
        val view = inflater.inflate(R.layout.fragment_rekap_absensi, container, false)
        initUi(view)
        initEvent()
        return view
    }

    fun initUi(view: View) {
        recyclerView = view.recyclerView
        etFrom = view.etFrom
        etTo = view.etTo
        btnCari = view.btnCari
        tvPlaceholder = view.tvPlaceholder
    }

    fun initEvent() {
        val calNow = Calendar.getInstance()
        calNow.time = Date()
        calNow.set(Calendar.DATE, calNow.getActualMinimum(Calendar.DATE))
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
        etFrom.setText(
            Constant.dateToSimpleString(
                calNow.time,
                SimpleDateFormat("yyyy-MM-dd")
            )
        )
        etTo.setText(
            Constant.dateToSimpleString(
                cal.time,
                SimpleDateFormat("yyyy-MM-dd")
            )
        )
        cari()
        val calMin = Calendar.getInstance()
        calMin.time = Date()
        calMin.add(Calendar.DATE, -365)
        val calMax = Calendar.getInstance()
        calMax.time = Date()
        calMax.add(Calendar.DATE, 365)
        val maxDate: Date = cal.time
        etFrom.keyListener = null
        etFrom.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clickDataPicker(etFrom, maxDate, calNow, calMin)
                etFrom.setOnClickListener {
                    clickDataPicker(
                        etFrom,
                        maxDate,
                        calNow, calMin
                    )
                }
            }
        }
        etTo.keyListener = null
        etTo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clickDataPicker(etTo, maxDate, calNow, calMin)
                etTo.setOnClickListener {
                    clickDataPicker(
                        etTo,
                        maxDate,
                        calNow, calMin
                    )
                }
            }
        }
        btnCari.setOnClickListener {
            cari()
        }
    }

    private fun cari() {
        val progress = ProgressDialog(context)
        progress.setTitle(getString(R.string.string_loading))
        progress.setMessage(getString(R.string.message_loading))
        progress.setCancelable(false)
        progress.show()
        ResponsePresensi.getPresensiRange(
            sharedPref.getProfile().no_induk,
            etFrom.text.toString(),
            etTo.text.toString()
        ).subscribe(
            {
                success->
                progress.dismiss()
                if (success.data.size >0){
                    recyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
                    tvPlaceholder.visibility = View.GONE
                    recyclerView.adapter = context?.let { RekapAbsensiAdapter(success.data, it) }
                }else{
                    tvPlaceholder.visibility = View.VISIBLE
                }
            },{
                error->
                progress.dismiss()
                context?.let { ServiceInterface.handleError(error, it) }
            }
        )
    }

    fun clickDataPicker(
        editText: TextInputEditText,
        maxDate: Date,
        calendar: Calendar,
        calMin: Calendar
    ) {
        val dpd = context?.let {
            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    //            if (y)

                    var date = ""
                    date += year.toString() + "-"
                    val bulan = monthOfYear + 1
                    if (monthOfYear < 10) {
                        date += "0" + bulan.toString() + "-"
                    } else {
                        date += bulan.toString() + "-"
                    }
                    if (dayOfMonth < 0) {
                        date += "0" + dayOfMonth.toString()
                    } else {
                        date += dayOfMonth.toString()
                    }

                    editText.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        dpd!!.datePicker.minDate = calMin.timeInMillis
        dpd.datePicker.maxDate = maxDate.time
        dpd.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RekapAbsensiFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RekapAbsensiFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
