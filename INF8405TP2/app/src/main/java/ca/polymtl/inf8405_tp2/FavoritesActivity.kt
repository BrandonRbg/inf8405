package ca.polymtl.inf8405_tp2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import ca.polymtl.inf8405_tp2.domain.Device
import ca.polymtl.inf8405_tp2.repositories.DevicesRepository
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.activity_favorites.*

class FavoritesActivity : AppCompatActivity() {

    private lateinit var devicesRepository: DevicesRepository

    private val favoriteDevices = mutableListOf<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureTheme()
        setContentView(R.layout.activity_favorites)

        devicesRepository = DevicesRepository(this)

        favoriteDevices.addAll(devicesRepository.findAll().filter { it.starred })

        devicesList.layoutManager = LinearLayoutManager(this)
        LastAdapter(favoriteDevices, BR.item)
            .map<Device>(R.layout.item_device)
            .into(devicesList)
    }
}
