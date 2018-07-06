package io.github.phearing.phearing.ui.history

import androidx.lifecycle.*
import io.github.phearing.phearing.room.audiometry.AudiometryData
import io.github.phearing.phearing.room.audiometry.AudiometryDataRepo
import java.util.*

class HistoryViewModel : ViewModel() {
    private val mHeadphoneRepo = AudiometryDataRepo()
    val time = MutableLiveData<Long>()
    private val mNextAudiometryData: LiveData<List<AudiometryData>> = Transformations.switchMap(time) {
        mHeadphoneRepo.loadNextAudiometryData(it, 10)
    }
    val audiometryData = MediatorLiveData<List<AudiometryData>>()

    init {
        time.value = Date().time
        audiometryData.value = mutableListOf()

        audiometryData.addSource(mNextAudiometryData) {
            it?.let {
                val list = mutableListOf<AudiometryData>()
                list.addAll(audiometryData.value ?: emptyList())
                list.addAll(it)
                audiometryData.value = list
            }
        }
    }
}
