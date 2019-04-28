package ca.polymtl.inf8405.ChatView

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import ca.polymtl.inf8405.BR
import ca.polymtl.inf8405.Domain.Message
import ca.polymtl.inf8405.R
import ca.polymtl.inf8405.Services.MessagingService
import ca.polymtl.inf8405.Utils.buildDrawer
import ca.polymtl.inf8405.Utils.currentUser
import com.github.nitrico.lastadapter.BaseType
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.TypeHandler
import com.google.firebase.firestore.ListenerRegistration
import com.mikepenz.materialdrawer.Drawer
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_chat_view.*
import javax.inject.Inject

class ChatViewActivity : AppCompatActivity(), SensorEventListener {

    @Inject
    lateinit var messagingService: MessagingService

    lateinit var adapter: LastAdapter
    lateinit var sensorManager: SensorManager
    var lightSensor: Sensor? = null
    var gravitySensor: Sensor? = null
    var currentLight: Float = 0.0f
    var currentGravity: Float = 0.0f

    var messageListenerRegistration: ListenerRegistration? = null
    var messages = mutableListOf<Message>()

    private lateinit var drawer: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_view)

        setSupportActionBar(toolbar)

        drawer = buildDrawer(toolbar, 0)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        messagesRv.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        adapter = LastAdapter(messages, BR.message)
            .handler(object : TypeHandler {
                override fun getItemType(item: Any, position: Int): BaseType? =
                    BaseType(
                        if (messages[position].sender == currentUser)
                            R.layout.item_message_sent
                        else
                            R.layout.item_message_received,
                        BR.message
                    )
            })
            .into(messagesRv)

        listenForMessages()

        sendButton.setOnClickListener {
            val text = messageEdit.text.toString()
            if (text.isNotEmpty()) {
                messagingService.sendMessage(text, currentUser)
                messageEdit.setText("")
            }
        }

        temperatureButton.setOnClickListener {
            if (lightSensor == null) {
                messageEdit.setText("Premièrement, ton device supporte pas la lumière")
            } else {
                messageEdit.setText("Premièrement, check comment que mon device est lumineux: $currentLight lx")
            }
        }

        gravityButton.setOnClickListener {
            if (gravitySensor == null) {
                messageEdit.setText("Ton device supporte pas la gravité")
            } else {
                messageEdit.setText("Check ma gravité! $currentGravity m/s2")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        messageListenerRegistration?.remove()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        listenForMessages()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun listenForMessages() {
        messageListenerRegistration = messagingService.listenForMessages {
            messages.clear()
            messages.addAll(it)

            adapter.notifyDataSetChanged()
            messagesRv.scrollToPosition(messages.size - 1)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // NO-OP
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.also {
            when (it.sensor.type) {
                Sensor.TYPE_LIGHT -> currentLight = it.values[0]
                Sensor.TYPE_GRAVITY -> currentGravity = Math.sqrt(it.values.map { it * it }.sum().toDouble()).toFloat()
            }
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }
}