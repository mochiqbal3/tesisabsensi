package co.id.tesis.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.id.distriboost.util.Constant
import co.id.distriboost.util.Converter
import co.id.tesis.R
import co.id.tesis.model.Presensi
import kotlinx.android.synthetic.main.default_card.view.*
import java.text.SimpleDateFormat


class RekapAbsensiAdapter(
    val items: MutableList<Presensi>,
    val context: Context
) : RecyclerView.Adapter<RekapAbsensiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.default_card,
                p0,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val presensi: Presensi = items.get(position)
        viewHolder.tvJamKeluar.text = presensi.jam_keluar
        viewHolder.tvJamMasuk.text = presensi.jam_masuk
        viewHolder.tvTanggal.text = Constant.dateToSimpleString(Constant.stringToDate(presensi.tanggal, SimpleDateFormat("yyyy-MM-dd")),SimpleDateFormat("dd MMM yyyy"))
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal = view.tvTanggal
        val tvJamMasuk = view.tvJamMasuk
        val tvJamKeluar = view.tvJamKeluar
        val item = view
    }
}