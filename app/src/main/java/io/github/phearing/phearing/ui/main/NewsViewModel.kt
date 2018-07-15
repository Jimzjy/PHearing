package io.github.phearing.phearing.ui.main

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Component
import io.github.phearing.phearing.common.*
import io.github.phearing.phearing.network.news.NewsRepo
import javax.inject.Inject
import javax.inject.Scope

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    val newsNavigation = MutableLiveData<List<NavigationDataShow>>()
    val newsData = MutableLiveData<List<NewsListDataShow>>()
    val newsListDataList: LiveData<List<NewsListData>>
    val newsNavigationDataList: LiveData<List<NavigationData>>
    val newsListDataImage: LiveData<Drawable>
    val newsNavigationDataImage: LiveData<Drawable>
    var isAddDataMode = false

    @Inject
    lateinit var newsRepo: NewsRepo

    init {
        DaggerNewsComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)
        newsListDataList = newsRepo.newsListDataList
        newsListDataImage = newsRepo.newsListDataImage
        newsNavigationDataList = newsRepo.newsNavigationDataList
        newsNavigationDataImage = newsRepo.newsNavigationDataImage
    }

    fun updateNews() {
        newsRepo.listNews()
    }

    fun updateNextNews() {
        if (newsRepo.newsListNext.isEmpty()) {
            newsRepo.newsListDataList.value = null
        } else {
            newsRepo.listNextNews()
        }
    }

    fun refreshNews(imageBuf: MutableList<Drawable?>?) {
        newsListDataList.value?.let {
            val data = mutableListOf<NewsListDataShow>()

            it.forEach {
                if (it.image.isNotEmpty()) {
                    data.add(NewsListDataShow(it.url, it.title, it.excerpt, imageBuf?.get(0)))
                    imageBuf?.removeAt(0)
                } else {
                    data.add(NewsListDataShow(it.url, it.title, it.excerpt, null))
                }
            }

            if (isAddDataMode) {
                val tmp = mutableListOf<NewsListDataShow>()
                newsData.value?.let {
                    tmp.addAll(it)
                    tmp.addAll(data)
                }
                newsData.value = tmp
            } else {
                newsData.value = data
            }
        }
    }

    fun getNewsImage(url: String) {
        newsRepo.getNewsListDataImage(url)
    }

    fun updateNavigation() {
        if (!isAddDataMode) {
            newsRepo.listNavigation()
        } else {
            newsRepo.newsNavigationDataList.value = null
        }
    }

    fun getNavigationImage(url: String) {
        newsRepo.getNewsNavigationDataImage(url)
    }

    fun refreshNavigation(imageBuf: MutableList<Drawable?>) {
        newsNavigationDataList.value?.let {
            val data = mutableListOf<NavigationDataShow>()

            it.forEach {
                if (it.image.isNotEmpty()) {
                    if (imageBuf[0] != null) {
                        data.add(NavigationDataShow(imageBuf[0]!!, it.contentUrl))
                        imageBuf.removeAt(0)
                    }
                }
            }

            newsNavigation.value = data
        }
    }
}

@NewsScope
@Component(dependencies = [ApplicationComponent::class])
interface NewsComponent {
    fun inject(newsViewModel: NewsViewModel)
    fun inject(contentViewModel: ContentViewModel)
    fun inject(searchActivity: SearchActivity)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class NewsScope
