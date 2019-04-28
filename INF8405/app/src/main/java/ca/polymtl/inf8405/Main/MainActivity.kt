package ca.polymtl.inf8405.Main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import ca.polymtl.inf8405.Main.ChatView.ChatViewFragment
import ca.polymtl.inf8405.Main.MapView.MapViewFragment
import ca.polymtl.inf8405.R
import ca.polymtl.inf8405.Utils.buildDrawer
import com.mikepenz.materialdrawer.Drawer
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var drawer: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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
                    else -> MapViewFragment()
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
}
