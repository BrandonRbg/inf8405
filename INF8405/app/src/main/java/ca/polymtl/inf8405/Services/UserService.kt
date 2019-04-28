package ca.polymtl.inf8405.Services

import android.content.Context
import android.location.Location
import ca.polymtl.inf8405.Domain.User
import ca.polymtl.inf8405.Utils.currentUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserService(
    private val firestore: FirebaseFirestore,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context
) {

    var lastLocation: Location? = null

    fun updateLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            lastLocation = it
            updateOrCreateUserLocation()
        }
    }

    fun updateOrCreateUserLocation() {
        val username = context.currentUser
        if (username.isEmpty()) {
            return
        }
        val user = User(
            username,
            lastLocation?.latitude ?: 0.0,
            lastLocation?.longitude ?: 0.0
        )
        firestore.collection("users")
            .whereEqualTo("username", username).get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    firestore.collection("users")
                        .add(user)
                } else {
                    val documentId = it.documents[0].id
                    firestore.collection("users")
                        .document(documentId)
                        .set(user)
                }
            }
    }

    fun listenForUsers(onUsersUpdate: (List<User>) -> Unit): ListenerRegistration {
        return firestore.collection("users")
            .addSnapshotListener { querySnapshot, e ->
                onUsersUpdate(
                    querySnapshot?.toObjects(User::class.java) ?: emptyList()
                )
            }
    }
}