package io.github.phearing.phearing.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.NewsDataRVAdapter
import io.github.phearing.phearing.common.NewsListDataShow
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.common.widget.LoadingDialog
import io.github.phearing.phearing.network.news.NewsRepo
import kotlinx.android.synthetic.main.activity_search.*
import javax.inject.Inject

class SearchActivity : AppCompatActivity() {
    private lateinit var mNewsDataRVAdapter: NewsDataRVAdapter
    private var mLoadingDialog: LoadingDialog? = null
    private var mIsReady = false

    private val mDataList = mutableListOf<NewsListDataShow>()

    @Inject
    lateinit var newsRepo: NewsRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        search_search_iv.setOnClickListener { onBackPressed() }

        DaggerNewsComponent.builder().applicationComponent(PHApplication.applicationComponent).build().inject(this)

        mNewsDataRVAdapter = NewsDataRVAdapter(this, mDataList)
        search_rv?.layoutManager = LinearLayoutManager(this)
        search_rv?.adapter = mNewsDataRVAdapter

        init()
    }

    override fun onStop() {
        super.onStop()
        mLoadingDialog?.dismiss()
    }

    private fun init() {
        search_search_et.setOnKeyListener { v, _, _ ->
            if (!mIsReady) {
                val view = (v as TextInputEditText)
                if (view.text.toString().isNotEmpty()) {
                    search(view.text.toString())
                }
            }
            false
        }
        mNewsDataRVAdapter.setOnClickCallback {
            val intent = Intent(this@SearchActivity, ContentActivity::class.java)
            intent.putExtra("url", it)
            startActivity(intent)
        }

        newsRepo.newsDataSearch.observe(this, Observer {
            if (mIsReady) {
                if (it == null) {
                    mIsReady = false
                    mLoadingDialog?.dismiss()
                } else {
                    mDataList.clear()
                    it.forEach {
                        mDataList.add(NewsListDataShow(it.url, it.title, it.excerpt, null))
                    }
                    mNewsDataRVAdapter.notifyDataSetChanged()

                    mIsReady = false
                    mLoadingDialog?.dismiss()
                }
            }
        })
    }

    private fun search(text: String) {
        mLoadingDialog = LoadingDialog.newInstance()
        mLoadingDialog?.isCancelable = false
        mLoadingDialog?.show(supportFragmentManager, "search_loadingDialog")

        mIsReady = true
        newsRepo.search(text)
    }
}
