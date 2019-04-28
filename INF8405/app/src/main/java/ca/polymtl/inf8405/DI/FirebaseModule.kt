package ca.polymtl.inf8405.DI

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@Singleton
class FirebaseModule {
    @Provides
    fun cloudFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}