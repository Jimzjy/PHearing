package io.github.phearing.phearing.network.news

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class NewsRepo {
    private val mNewsService = NewsService.create()

    val newsListDataList = MutableLiveData<List<NewsListData>>()
    val newsListDataImage = MutableLiveData<Drawable>()
    val newsNavigationDataList = MutableLiveData<List<NavigationData>>()
    val newsNavigationDataImage = MutableLiveData<Drawable>()
    val newsData = MutableLiveData<NewsData>()
    val newsDataSearch = MutableLiveData<List<NewsListData>>()
    var newsListNext: String = ""
        private set

    fun listNews() {
        mNewsService.listNews().enqueue(object : Callback<Page<NewsListData>> {
            override fun onFailure(call: Call<Page<NewsListData>>?, t: Throwable?) {
                makeToast(R.string.news_loading_failed, true)
            }

            override fun onResponse(call: Call<Page<NewsListData>>?, response: Response<Page<NewsListData>>?) {
                response?.body()?.let {
                    newsListDataList.value = it.results
                    newsListNext = it.next ?: ""
                }
            }
        })
    }

    fun listNextNews() {
        mNewsService.listNextNews(newsListNext).enqueue(object : Callback<Page<NewsListData>> {
            override fun onFailure(call: Call<Page<NewsListData>>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<Page<NewsListData>>?, response: Response<Page<NewsListData>>?) {
                response?.body()?.let {
                    newsListDataList.value = it.results
                    newsListNext = it.next ?: ""
                }
            }
        })
    }

    fun getNewsListDataImage(url: String) {
        mNewsService.getImage(url).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                newsListDataImage.value = null
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.let {
                    if (!it.isSuccessful) {
                        newsListDataImage.value = null
                        return@let
                    }
                    val file = File(PHApplication.instance.getExternalFilesDir(null), url.split("/").last())
                    if (file.exists()) file.delete()
                    var outputStream: FileOutputStream? = null
                    var inputStream: InputStream? = null
                    try {
                        val buf = ByteArray(4096)
                        outputStream = FileOutputStream(file)

                        it.body()?.let {
                            inputStream = it.byteStream()

                            while (true) {
                                val i: Int = inputStream?.read(buf) ?: -1
                                if (i == -1) break
                                outputStream.write(buf, 0, i)
                            }
                            outputStream.flush()

                            val bitmapDrawable = BitmapDrawable(PHApplication.instance.resources, file.path)
                            newsListDataImage.value = bitmapDrawable
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        newsListDataImage.value = null
                    } finally {
                        outputStream?.close()
                        inputStream?.close()
                    }
                }
            }
        })
    }

    fun listNavigation() {
        mNewsService.listNavigation().enqueue(object : Callback<List<NavigationData>> {
            override fun onFailure(call: Call<List<NavigationData>>?, t: Throwable?) {
                makeToast(R.string.news_loading_failed, true)
            }

            override fun onResponse(call: Call<List<NavigationData>>?, response: Response<List<NavigationData>>?) {
                response?.body()?.let {
                    newsNavigationDataList.value = it
                }
            }
        })
    }

    fun getNewsNavigationDataImage(url: String) {
        mNewsService.getImage(url).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                newsNavigationDataImage.value = null
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.let {
                    if (!it.isSuccessful) {
                        newsNavigationDataImage.value = null
                        return@let
                    }
                    val file = File(PHApplication.instance.getExternalFilesDir(null), url.split("/").last())
                    if (file.exists()) file.delete()
                    var outputStream: FileOutputStream? = null
                    var inputStream: InputStream? = null
                    try {
                        val buf = ByteArray(4096)
                        outputStream = FileOutputStream(file)

                        it.body()?.let {
                            inputStream = it.byteStream()

                            while (true) {
                                val i: Int = inputStream?.read(buf) ?: -1
                                if (i == -1) break
                                outputStream.write(buf, 0, i)
                            }
                            outputStream.flush()

                            val bitmapDrawable = BitmapDrawable(PHApplication.instance.resources, file.path)
                            newsNavigationDataImage.value = bitmapDrawable
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        newsNavigationDataImage.value = null
                    } finally {
                        outputStream?.close()
                        inputStream?.close()
                    }
                }
            }
        })
    }

    fun getNews(url: String) {
        mNewsService.getNews(url).enqueue(object : Callback<NewsData> {
            override fun onFailure(call: Call<NewsData>?, t: Throwable?) {
                makeToast(R.string.news_loading_failed, true)
                newsData.value = null
            }

            override fun onResponse(call: Call<NewsData>?, response: Response<NewsData>?) {
                response?.body()?.let {
                    newsData.value = it
                }
            }
        })
    }

    fun search(text: String) {
        mNewsService.search(text).enqueue(object : Callback<Page<NewsListData>> {
            override fun onFailure(call: Call<Page<NewsListData>>?, t: Throwable?) {
                newsDataSearch.value = null
            }

            override fun onResponse(call: Call<Page<NewsListData>>?, response: Response<Page<NewsListData>>?) {
                response?.body()?.let {
                    newsDataSearch.value = it.results
                }
            }
        })
    }

    private fun makeToast(resId: Int, isLong: Boolean) {
        if (isLong) {
            Toast.makeText(PHApplication.instance.applicationContext,
                    PHApplication.instance.applicationContext.getString(resId),
                    Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(PHApplication.instance.applicationContext,
                    PHApplication.instance.applicationContext.getString(resId),
                    Toast.LENGTH_SHORT).show()
        }
    }
}