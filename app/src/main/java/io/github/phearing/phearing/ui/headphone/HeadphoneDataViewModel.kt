package io.github.phearing.phearing.ui.headphone

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.Component
import io.github.phearing.phearing.common.ApplicationComponent
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.room.headphone.Headphone
import io.github.phearing.phearing.room.headphone.HeadphoneRepo
import javax.inject.Inject

class HeadphoneDataViewModel : ViewModel() {
    @Inject
    lateinit var headphoneRepo: HeadphoneRepo
    val allHeadphones: LiveData<List<Headphone>>

    init {
        DaggerHeadphoneDataViewModelComponent.builder()
                .applicationComponent(PHApplication.applicationComponent).build().inject(this)

        allHeadphones = headphoneRepo.allHeadphones
    }

    fun deleteHeadphone(position: Int) {
        allHeadphones.value?.let {
            headphoneRepo.deleteHeadphones(it[position])
        }
    }
}

@MeasureScope
@Component(dependencies = [ApplicationComponent::class])
interface HeadphoneDataViewModelComponent {
    fun inject(headphoneDataViewModel: HeadphoneDataViewModel)
}