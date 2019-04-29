package ca.polymtl.inf8405.DI

import ca.polymtl.inf8405.Main.ChatView.ChatViewFragment
import ca.polymtl.inf8405.Main.ChatView.ChatViewFragmentModule
import ca.polymtl.inf8405.Main.MapView.MapViewFragment
import ca.polymtl.inf8405.Main.MapView.MapViewFragmentModule
import ca.polymtl.inf8405.Main.MapView.StatsViewFragmentModule
import ca.polymtl.inf8405.Main.StatsView.StatsViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {
    @ContributesAndroidInjector(modules = [MapViewFragmentModule::class])
    abstract fun bindMapViewFragment(): MapViewFragment

    @ContributesAndroidInjector(modules = [ChatViewFragmentModule::class])
    abstract fun bindChatViewFragment(): ChatViewFragment

    @ContributesAndroidInjector(modules = [StatsViewFragmentModule::class])
    abstract fun bindStatsViewFragment(): StatsViewFragment
}