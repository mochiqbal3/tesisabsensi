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
import co.id.tesis.model.Router
import kotlinx.android.synthetic.main.default_card.view.*
import java.text.SimpleDateFormat


class MacAddressAdapter(
    val items: MutableList<Router>,
    val context: Context
) : RecyclerView.Adapter<MacAddressAdapter.ViewHolder>() {

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
        val presensi: Router = items.get(position)
        viewHolder.labelSatu.text = "Latitude"
        viewHolder.labelDua.text = "Longitude"
        viewHolder.tvJamKeluar.text = presensi.lat.toString()
        viewHolder.tvJamMasuk.text = presensi.lng.toString()
        viewHolder.tvTanggal.text = presensi.nama
        viewHolder.tvTitle.text = presensi.mac_address
        viewHolder.labelTwoDotes.text = " | "
        viewHolder.labelTwoDotes
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal = view.tvTanggal
        val tvJamMasuk = view.tvJamMasuk
        val tvJamKeluar = view.tvJamKeluar
        val labelSatu = view.labelSatu
        val labelDua = view.labelDua
        val labelTwoDotes = view.labelTwoDotes
        val tvTitle = view.tvTitle
        val item = view
    }
}