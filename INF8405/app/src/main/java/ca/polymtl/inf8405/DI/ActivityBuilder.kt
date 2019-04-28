package ca.polymtl.inf8405.DI

import ca.polymtl.inf8405.Main.MainActivity
import ca.polymtl.inf8405.Main.MainActivityModule
import ca.polymtl.inf8405.Onboarding.OnboardingActivity
import ca.polymtl.inf8405.Onboarding.OnboardingActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = arrayOf(OnboardingActivityModule::class))
    internal abstract fun bindOnboardingActivity(): OnboardingActivity

    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    internal abstract fun bindMainActivity(): MainActivity
}