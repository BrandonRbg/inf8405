package ca.polymtl.inf8405_tp2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Timer().schedule(3000) {
            startActivity(intentFor<MapActivity>().clearTop())
        }
    }
}
