package ca.polymtl.inf8405.Onboarding

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ca.polymtl.inf8405.Main.ChatView.ChatViewFragment
import ca.polymtl.inf8405.R
import ca.polymtl.inf8405.Utils.currentUser
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.jetbrains.anko.intentFor

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        usernameEdit.setText(currentUser)

        usernameButton.setOnClickListener {
            val text = usernameEdit.text.toString()
            if (text.isNotEmpty()) {
                currentUser = text
                startActivity(intentFor<ChatViewFragment>())
            }
        }
    }
}
