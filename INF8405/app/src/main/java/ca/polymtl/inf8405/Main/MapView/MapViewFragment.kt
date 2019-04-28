package ca.polymtl.inf8405.Main.MapView

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.polymtl.inf8405.R
import ca.polymtl.inf8405.Services.UserService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_map_view.view.*
import javax.inject.Inject


class MapViewFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView

    @Inject
    lateinit var userService: UserService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_view, container, false)
        mapView = view.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync {
            map = it
            listenForUsers()
        }
        return view
    }

    fun listenForUsers() {
        userService.listenForUsers {
            map.clear()
            if (userService.lastLocation != null) {
                val currentLatLng =
                    LatLng(userService.lastLocation?.latitude ?: 0.0, userService.lastLocation?.longitude ?: 0.0)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
            }
            it.forEach { user ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(user.latitude, user.longitude))
                        .title(user.username)
                )
            }
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
