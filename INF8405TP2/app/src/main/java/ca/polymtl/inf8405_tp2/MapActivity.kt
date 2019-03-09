package ca.polymtl.inf8405_tp2

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import ca.polymtl.inf8405_tp2.domain.Device
import com.github.nitrico.lastadapter.LastAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.alert

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        updateDevicesList(listOf(
            Device(0, 0, "Device #1", "B9-42-6C-CA-4A-30", "", ""),
            Device(0, 0, "Device #2", "03-AD-CE-06-6C-25", "", ""),
            Device(0, 0, "Device #3", "17-9D-A0-22-62-9C", "", ""),
            Device(0, 0, "Device #1", "B9-42-6C-CA-4A-30", "", ""),
            Device(0, 0, "Device #2", "03-AD-CE-06-6C-25", "", ""),
            Device(0, 0, "Device #3", "17-9D-A0-22-62-9C", "", ""),
            Device(0, 0, "Device #1", "B9-42-6C-CA-4A-30", "", ""),
            Device(0, 0, "Device #2", "03-AD-CE-06-6C-25", "", ""),
            Device(0, 0, "Device #3", "17-9D-A0-22-62-9C", "", ""),
            Device(0, 0, "Device #1", "B9-42-6C-CA-4A-30", "", ""),
            Device(0, 0, "Device #2", "03-AD-CE-06-6C-25", "", ""),
            Device(0, 0, "Device #3", "17-9D-A0-22-62-9C", "", ""),
            Device(0, 0, "Device #1", "B9-42-6C-CA-4A-30", "", ""),
            Device(0, 0, "Device #2", "03-AD-CE-06-6C-25", "", ""),
            Device(0, 0, "Device #3", "17-9D-A0-22-62-9C", "", ""),
            Device(0, 0, "Device #1", "B9-42-6C-CA-4A-30", "", ""),
            Device(0, 0, "Device #2", "03-AD-CE-06-6C-25", "", ""),
            Device(0, 0, "Device #3", "17-9D-A0-22-62-9C", "", ""),
            Device(0, 0, "Device #1", "B9-42-6C-CA-4A-30", "", ""),
            Device(0, 0, "Device #2", "03-AD-CE-06-6C-25", "", ""),
            Device(0, 0, "Device #3", "17-9D-A0-22-62-9C", "", ""),
            Device(0, 0, "Device #4", "BF-3C-36-2F-B5-61", "", "")
        ))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        map.isBuildingsEnabled = true

        setupMap()
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
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    private fun updateDevicesList(devices: List<Device>) {
        devicesList.layoutManager = LinearLayoutManager(this)
        LastAdapter(devices, BR.item)
            .map<Device>(R.layout.item_device)
            .into(devicesList)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
