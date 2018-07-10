package io.github.phearing.phearing.ui.main

import android.app.Application
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.NewsData

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    val newsNavigation = MutableLiveData<List<Drawable>>()
    val newsData = MutableLiveData<List<NewsData>>()

    init {
        newsNavigation.value = listOf(
                ContextCompat.getDrawable(application, R.drawable.vector_drawable_clear)!!,
                ContextCompat.getDrawable(application, R.drawable.vector_drawable_check)!!,
                ContextCompat.getDrawable(application, R.drawable.vector_drawable_back)!!
        )
        newsData.value = listOf(
                NewsData("Android Studio 3.2 Beta 版本已经发布", "Android Studio 3.2 Beta 版本已经发布。官方 Android IDE 的最新版本可帮助开发者快速上手Android Jetpack、Android P 开发者预览版以及新的 Android App Bundle 格式。它还包括 Emulator Snapshots 和 Energy Profiler",
                        ContextCompat.getDrawable(application, R.drawable.vector_drawable_check)!!),
                NewsData("Android Studio 3.2 Beta 版本已经发布", "Android Studio 3.2 Beta 版本已经发布。官方 Android IDE 的最新版本可帮助开发者快速上手Android Jetpack、Android P 开发者预览版以及新的 Android App Bundle 格式。它还包括 Emulator Snapshots 和 Energy Profiler",
                        null),
                NewsData("Android Studio 3.2 Beta 版本已经发布", "Android Studio 3.2 Beta 版本已经发布。官方 Android IDE 的最新版本可帮助开发者快速上手Android Jetpack、Android P 开发者预览版以及新的 Android App Bundle 格式。它还包括 Emulator Snapshots 和 Energy Profiler",
                        ContextCompat.getDrawable(application, R.drawable.vector_drawable_sun)!!),
                NewsData("Android Studio 3.2 Beta 版本已经发布", "Android Studio 3.2 Beta 版本已经发布。官方 Android IDE 的最新版本可帮助开发者快速上手Android Jetpack、Android P 开发者预览版以及新的 Android App Bundle 格式。它还包括 Emulator Snapshots 和 Energy Profiler",
                        null),
                NewsData("Android Studio 3.2 Beta 版本已经发布", "Android Studio 3.2 Beta 版本已经发布。官方 Android IDE 的最新版本可帮助开发者快速上手Android Jetpack、Android P 开发者预览版以及新的 Android App Bundle 格式。它还包括 Emulator Snapshots 和 Energy Profiler",
                        null),
                NewsData("Android Studio 3.2 Beta 版本已经发布", "Android Studio 3.2 Beta 版本已经发布。官方 Android IDE 的最新版本可帮助开发者快速上手Android Jetpack、Android P 开发者预览版以及新的 Android App Bundle 格式。它还包括 Emulator Snapshots 和 Energy Profiler",
                        null),
                NewsData("Android Studio 3.2 Beta 版本已经发布", "Android Studio 3.2 Beta 版本已经发布。官方 Android IDE 的最新版本可帮助开发者快速上手Android Jetpack、Android P 开发者预览版以及新的 Android App Bundle 格式。它还包括 Emulator Snapshots 和 Energy Profiler",
                        null),
                NewsData("Android Studio 3.2 Beta 版本已经发布", "Android Studio 3.2 Beta 版本已经发布。官方 Android IDE 的最新版本可帮助开发者快速上手Android Jetpack、Android P 开发者预览版以及新的 Android App Bundle 格式。它还包括 Emulator Snapshots 和 Energy Profiler",
                        null)
        )
    }
}
