package ca.polymtl.inf8405.Main

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import ca.polymtl.inf8405.Main.ChatView.ChatViewFragment
import ca.polymtl.inf8405.Main.MapView.MapViewFragment
import ca.polymtl.inf8405.Main.StatsView.StatsViewFragment
import ca.polymtl.inf8405.R
import ca.polymtl.inf8405.Services.UserService
import ca.polymtl.inf8405.Utils.buildDrawer
import com.mikepenz.materialdrawer.Drawer
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var userService: UserService

    private lateinit var drawer: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            userService.updateLocation()
        }

        drawer = buildDrawer(toolbar, 0) { item ->
            switchFragment(item)
        }

        switchFragment(0)
    }

    private fun switchFragment(id: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                when (id) {
                    0 -> ChatViewFragment()
                    1 -> MapViewFragment()
                    else -> StatsViewFragment()
                }
            ).commit()
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        }
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        userService.updateLocation()
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
