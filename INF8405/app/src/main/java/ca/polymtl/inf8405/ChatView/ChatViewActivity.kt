package ca.polymtl.inf8405.ChatView

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import ca.polymtl.inf8405.BR
import ca.polymtl.inf8405.Domain.Message
import ca.polymtl.inf8405.R
import ca.polymtl.inf8405.Services.MessagingService
import ca.polymtl.inf8405.Utils.currentUser
import com.github.nitrico.lastadapter.BaseType
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.TypeHandler
import com.google.firebase.firestore.ListenerRegistration
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_chat_view.*
import javax.inject.Inject

class ChatViewActivity : AppCompatActivity() {
    @Inject
    lateinit var messagingService: MessagingService

    lateinit var adapter: LastAdapter

    var messageListenerRegistration: ListenerRegistration? = null

    var messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_view)

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
    }

    override fun onStop() {
        super.onStop()
        messageListenerRegistration?.remove()
    }

    private fun listenForMessages() {
        messageListenerRegistration = messagingService.listenForMessages {
            messages.clear()
            messages.addAll(it)

            adapter.notifyDataSetChanged()
            messagesRv.scrollToPosition(messages.size - 1)
        }
    }
}