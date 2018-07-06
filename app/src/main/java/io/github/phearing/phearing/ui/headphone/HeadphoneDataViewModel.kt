package io.github.phearing.phearing.ui.headphone

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.phearing.phearing.room.headphone.Headphone
import io.github.phearing.phearing.room.headphone.HeadphoneRepo

class HeadphoneDataViewModel : ViewModel() {
    val allHeadphones: LiveData<List<Headphone>> = HeadphoneRepo().allHeadphones
}