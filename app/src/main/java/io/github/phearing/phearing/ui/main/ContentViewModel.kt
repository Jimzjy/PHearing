package io.github.phearing.phearing.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.phearing.phearing.common.NewsData
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.network.news.NewsRepo
import javax.inject.Inject

class ContentViewModel : ViewModel() {
    var url = ""
    val newsData: LiveData<NewsData>

    @Inject
    lateinit var newsRepo: NewsRepo

    init {
        DaggerNewsComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)
        newsData = newsRepo.newsData
    }

    fun getNews() {
        newsRepo.getNews(url)
    }
}
