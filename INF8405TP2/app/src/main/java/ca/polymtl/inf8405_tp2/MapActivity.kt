package ca.polymtl.inf8405_tp2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import ca.polymtl.inf8405_tp2.bluetooth.BLEWrapper
import ca.polymtl.inf8405_tp2.databinding.ItemDeviceBinding
import ca.polymtl.inf8405_tp2.domain.Device
import ca.polymtl.inf8405_tp2.domain.majorDeviceClasses
import ca.polymtl.inf8405_tp2.repositories.DevicesRepository
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var bleWrapper: BLEWrapper
    private lateinit var devicesRepository: DevicesRepository
    private lateinit var adapter: LastAdapter

    private val devices = mutableListOf<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureTheme()

        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        devicesRepository = DevicesRepository(this)

        setupDevicesList()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        favoritesButton.setOnClickListener {
            startActivity(intentFor<FavoritesActivity>())
        }

        themeButton.setOnClickListener {
            lightTheme = !lightTheme
            recreate()
        }
    }

    fun onNewDevice(device: BluetoothDevice) {
        if (devices.find { it.address == device.address } != null) return
        lastLocation?.also {
            val device = Device(
                it.latitude,
                it.longitude,
                device.name ?: "Unnamed device",
                device.address,
                majorDeviceClasses.find { it.id == device.bluetoothClass.majorDeviceClass }?.name ?: "",
                when (device.type) {
                    DEVICE_TYPE_CLASSIC -> "Classic - BR/EDR devices"
                    DEVICE_TYPE_LE -> "Low Energy - LE-only"
                    DEVICE_TYPE_DUAL -> "Dual Mode - BR/EDR/LE"
                    else -> "Unknown"
                },
                false
            )
            devices.add(device)
            devicesRepository.create(device)

            adapter.notifyItemInserted(devices.size - 1)
            map.addMarker(MarkerOptions()
                .position(LatLng(it.latitude, it.longitude))
                .title(device.address)
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        map.isBuildingsEnabled = true
        map.isIndoorEnabled = false
        map.setOnMarkerClickListener(this)

        devices.forEach {
            map.addMarker(MarkerOptions()
                .position(LatLng(it.latitude, it.longitude))
                .title(it.address)
            )
        }

        setupMap()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.also {
            val device = devices.find { it.address == marker.title }
            onDeviceClick(device)
        }
        return true
    }

    private fun onDeviceClick(device: Device?) {
        device?.also { device ->
            val deviceInfo = """
                    ${device.address}
                    Lat: ${device.latitude}
                    Lng: ${device.longitude}
                    Classe d'appareil: ${device.deviceClass}
                    Type d'appareil: ${device.type}
                """.trimIndent()
            alert {
                positiveButton("Partager") {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_SUBJECT, "Hey check out ${device.name}!")
                        putExtra(Intent.EXTRA_TEXT, "${device.name}\n" + deviceInfo)
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, "VIM App - Share Via"))
                }
                negativeButton("Comment y arriver?") {
                    val gmmIntentUri = Uri.parse("google.navigation:q=${device.latitude},${device.longitude}&mode=d")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.`package` = "com.google.android.apps.maps"
                    startActivity(mapIntent)
                }
                neutralPressed("Ajouter aux favoris") {
                    devicesRepository.setStarred(device)
                }
                title = device.name
                message = deviceInfo
            }.show()
        }
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            updateLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        updateLocation()
                    }
                } else {
                    alert("Please enable location.").show()
                }
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            val last = lastLocation
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
            if (last == null) {
                startBluetoothScan()
            }
        }
    }

    private fun startBluetoothScan() {
        bleWrapper = BLEWrapper(this) {
            onNewDevice(it)
        }

        bleWrapper.scan(this)
    }

    private fun setupDevicesList() {
        devicesList.layoutManager = LinearLayoutManager(this)
        val deviceType = Type<ItemDeviceBinding>(R.layout.item_device)
            .onClick {
                onDeviceClick(it.binding.item)
            }

        devices.addAll(devicesRepository.findAll())

        adapter = LastAdapter(devices, BR.item)
            .map<Device>(deviceType)
            .into(devicesList)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
