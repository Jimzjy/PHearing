package io.github.phearing.phearing.common.application

import android.app.Application

class PHApplication: Application() {
    companion object {
        @JvmStatic
        lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this)).build()
    }
}