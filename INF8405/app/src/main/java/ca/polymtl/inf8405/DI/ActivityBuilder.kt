package ca.polymtl.inf8405.DI

import ca.polymtl.inf8405.ChatView.ChatViewActivity
import ca.polymtl.inf8405.ChatView.ChatViewActivityModule
import ca.polymtl.inf8405.Onboarding.OnboardingActivity
import ca.polymtl.inf8405.Onboarding.OnboardingActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = arrayOf(OnboardingActivityModule::class))
    internal abstract fun bindOnboardingActivity(): OnboardingActivity

    @ContributesAndroidInjector(modules = arrayOf(ChatViewActivityModule::class))
    internal abstract fun bindChatViewActivity(): ChatViewActivity
}