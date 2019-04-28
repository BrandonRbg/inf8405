package ca.polymtl.inf8405.DI

import android.app.Application
import android.content.Context
import ca.polymtl.inf8405.Services.MessagingService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [FirebaseModule::class])
class AppModule {
    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    internal fun provideMessagingService(firebaseFirestore: FirebaseFirestore): MessagingService =
        MessagingService(firebaseFirestore)
}