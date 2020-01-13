package co.id.tesis

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.id.tesis.adapter.MacAddressAdapter
import co.id.tesis.model.ResponseMacAddress
import com.gammasolution.segarmart.util.connection.ServiceInterface
import kotlinx.android.synthetic.main.fragment_rekap_absensi.*

class MainMenuAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu_admin)
        getMacAddress()
    }

    private fun getMacAddress() {
        val progress = ProgressDialog(this)
        progress.setTitle(getString(R.string.string_loading))
        progress.setMessage(getString(R.string.message_loading))
        progress.setCancelable(false)
        progress.show()
        ResponseMacAddress.getMacAddress().subscribe(
            { success ->
                progress.dismiss()
                recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
                recyclerView.adapter = MacAddressAdapter(success.data,this)
            }, { error ->
                progress.dismiss()
                ServiceInterface.handleError(error, this)
            }
        )
    }
}
