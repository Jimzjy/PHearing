package io.github.phearing.phearing.network.news

import io.github.phearing.phearing.common.NavigationData
import io.github.phearing.phearing.common.NewsData
import io.github.phearing.phearing.common.NewsListData
import io.github.phearing.phearing.common.Page
import io.github.phearing.phearing.network.user.BASE_URL
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface NewsService {
    companion object {
        fun create(): NewsService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(NewsService::class.java)
        }
    }

    @GET("news/")
    fun listNews(): Call<Page<NewsListData>>

    @GET
    fun listNextNews(@Url url: String): Call<Page<NewsListData>>

    @GET
    fun getNews(@Url url: String): Call<NewsData>

    @GET("navigation/")
    fun listNavigation(): Call<List<NavigationData>>

    @GET("news/")
    fun search(@Query("search") search: String): Call<Page<NewsListData>>

    @GET
    fun getImage(@Url url: String): Call<ResponseBody>
}