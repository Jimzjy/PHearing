package io.github.phearing.phearing.common

import android.app.Application
import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.phearing.phearing.network.news.NewsRepo
import io.github.phearing.phearing.network.user.UserRepo
import io.github.phearing.phearing.room.audiometry.AudiometryDataRepo
import io.github.phearing.phearing.room.headphone.HeadphoneRepo
import io.github.phearing.phearing.room.speech.SpeechDataRepo
import javax.inject.Scope

class PHApplication: Application() {
    companion object {
        lateinit var instance: PHApplication

        @JvmStatic
        lateinit var applicationComponent: ApplicationComponent
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this)).build()
    }
}

@Module
class ApplicationModule(private val application: PHApplication) {
    @Provides
    @ApplicationScope
    fun provideHeadphoneRepo() = HeadphoneRepo()

    @Provides
    @ApplicationScope
    fun provideAudiometryDataRepo() = AudiometryDataRepo()

    @Provides
    @ApplicationScope
    fun provideSpeechDataRepo() = SpeechDataRepo()

    @Provides
    @ApplicationScope
    fun provideUserRepo() = UserRepo()

    @Provides
    @ApplicationScope
    fun provideNewsRepo() = NewsRepo()
}

@ApplicationScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun headphoneRepo(): HeadphoneRepo
    fun audiometryDataRepo(): AudiometryDataRepo
    fun speechDataRepo(): SpeechDataRepo
    fun userRepo(): UserRepo
    fun newsRepo(): NewsRepo
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope