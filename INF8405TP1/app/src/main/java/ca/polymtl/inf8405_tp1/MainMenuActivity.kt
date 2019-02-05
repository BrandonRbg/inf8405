package ca.polymtl.inf8405_tp1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        playButton.setOnClickListener {
            startActivity(intentFor<MainActivity>())
        }

        aboutButton.setOnClickListener {
            alert("Brandon Roberge & Simon Lacasse", "Application par:").show()
        }

        exitButton.setOnClickListener {
            finishAndRemoveTask()
        }
    }
}
