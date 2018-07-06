package io.github.phearing.phearing.common.application

import android.content.Context
import dagger.Component

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun applicationContext(): Context
}