package ca.polymtl.inf8405.Services

import ca.polymtl.inf8405.Domain.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.*

class MessagingService(private val firestore: FirebaseFirestore) {
    fun listenForMessages(onNewMessage: (List<Message>) -> Unit): ListenerRegistration {
        return firestore.collection("messages")
            .addSnapshotListener { querySnapshot, e ->
                onNewMessage(
                    querySnapshot?.toObjects(Message::class.java)?.sortedBy { it.timestamp } ?: emptyList()
                )
            }
    }

    fun sendMessage(text: String, sender: String) {
        firestore.collection("messages")
            .add(Message(text, sender, Date().time))
    }
}