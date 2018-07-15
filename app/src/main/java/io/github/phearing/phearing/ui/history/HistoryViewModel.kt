package io.github.phearing.phearing.ui.history

import androidx.lifecycle.*
import dagger.Component
import io.github.phearing.phearing.common.ApplicationComponent
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.network.user.UserRepo
import io.github.phearing.phearing.room.audiometry.AudiometryData
import io.github.phearing.phearing.room.audiometry.AudiometryDataRepo
import io.github.phearing.phearing.room.speech.SpeechData
import io.github.phearing.phearing.room.speech.SpeechDataRepo
import java.text.FieldPosition
import java.util.*
import javax.inject.Inject
import javax.inject.Scope

class HistoryViewModel : ViewModel() {
    @Inject
    lateinit var audiometryDataRepo: AudiometryDataRepo
    @Inject
    lateinit var speechDataRepo: SpeechDataRepo
    @Inject
    lateinit var userRepo: UserRepo

    val audiometryData: LiveData<List<AudiometryData>>
    val speechData: LiveData<List<SpeechData>>

    val audiometryDataLiveData: LiveData<AudiometryData>
    val speechDataLiveData: LiveData<SpeechData>

    init {
        DaggerHistoryViewModelComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)
        audiometryDataLiveData = userRepo.audiometryDataLiveData
        speechDataLiveData = userRepo.speechDataLiveData

        audiometryData = audiometryDataRepo.allAudiometryData
        speechData = speechDataRepo.allSpeechData
    }

    fun deleteAudiometryData(position: Int) {
        audiometryData.value?.let {
            audiometryDataRepo.deleteAudiometryData(it[position])
        }
    }

    fun updateAudiometryData(audiometryData: AudiometryData) {
        audiometryDataRepo.updateAudiometryData(audiometryData)
    }

    fun deleteSpeechData(position: Int) {
        speechData.value?.let {
            speechDataRepo.deleteSpeechData(it[position])
        }
    }

    fun updateSpeechData(speechData: SpeechData) {
        speechDataRepo.updateSpeechData(speechData)
    }

    fun uploadAudiometryData(audiometryData: AudiometryData) {
        userRepo.uploadAudiometryData(audiometryData)
    }

    fun uploadSpeechData(speechData: SpeechData) {
        userRepo.uploadSpeechData(speechData)
    }
}

@HistoryScope
@Component(dependencies = [ApplicationComponent::class])
interface HistoryViewModelComponent {
    fun inject(historyViewModel: HistoryViewModel)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class HistoryScope
