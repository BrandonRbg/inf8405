package ca.polymtl.inf8405.Onboarding

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ca.polymtl.inf8405.Main.MainActivity
import ca.polymtl.inf8405.R
import ca.polymtl.inf8405.Services.UserService
import ca.polymtl.inf8405.Utils.currentUser
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.jetbrains.anko.intentFor
import javax.inject.Inject

class OnboardingActivity : AppCompatActivity() {

    @Inject
    lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        usernameEdit.setText(currentUser)

        usernameButton.setOnClickListener {
            val text = usernameEdit.text.toString()
            if (text.isNotEmpty()) {
                currentUser = text
                userService.updateOrCreateUserLocation()
                startActivity(intentFor<MainActivity>())
            }
        }
    }
}
