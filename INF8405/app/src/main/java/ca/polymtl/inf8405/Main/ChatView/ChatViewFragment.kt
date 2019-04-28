package ca.polymtl.inf8405.Main.ChatView

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.polymtl.inf8405.BR
import ca.polymtl.inf8405.Domain.Message
import ca.polymtl.inf8405.R
import ca.polymtl.inf8405.Services.MessagingService
import ca.polymtl.inf8405.Utils.currentUser
import com.github.nitrico.lastadapter.BaseType
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.TypeHandler
import com.google.firebase.firestore.ListenerRegistration
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_chat_view.view.*
import javax.inject.Inject

class ChatViewFragment : Fragment(), SensorEventListener {

    @Inject
    lateinit var messagingService: MessagingService

    lateinit var adapter: LastAdapter
    lateinit var sensorManager: SensorManager
    lateinit var messagesRv: RecyclerView
    var lightSensor: Sensor? = null
    var gravitySensor: Sensor? = null
    var currentLight: Float = 0.0f
    var currentGravity: Float = 0.0f

    var messageListenerRegistration: ListenerRegistration? = null
    var messages = mutableListOf<Message>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_chat_view, container, false)

        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        messagesRv = view.messagesRv

        messagesRv.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }

        adapter = LastAdapter(messages, BR.message)
            .handler(object : TypeHandler {
                override fun getItemType(item: Any, position: Int): BaseType? =
                    BaseType(
                        if (messages[position].sender == context?.currentUser)
                            R.layout.item_message_sent
                        else
                            R.layout.item_message_received,
                        BR.message
                    )
            })
            .into(messagesRv)

        listenForMessages()

        view.sendButton.setOnClickListener {
            val text = view.messageEdit.text.toString()
            if (text.isNotEmpty()) {
                messagingService.sendMessage(text, context?.currentUser ?: "")
                view.messageEdit.setText("")
            }
        }

        view.temperatureButton.setOnClickListener {
            if (lightSensor == null) {
                view.messageEdit.setText("Premièrement, ton device supporte pas la lumière")
            } else {
                view.messageEdit.setText("Premièrement, check comment que mon device est lumineux: $currentLight lx")
            }
        }

        view.gravityButton.setOnClickListener {
            if (gravitySensor == null) {
                view.messageEdit.setText("Ton device supporte pas la gravité")
            } else {
                view.messageEdit.setText("Check ma gravité! $currentGravity m/s2")
            }
        }

        return view
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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
}