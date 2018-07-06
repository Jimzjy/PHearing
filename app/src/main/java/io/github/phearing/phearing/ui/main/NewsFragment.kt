package io.github.phearing.phearing.ui.main

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.NewsData
import io.github.phearing.phearing.common.NewsDataRVAdapter
import io.github.phearing.phearing.common.NewsNavigationRVAdapter
import io.github.phearing.phearing.common.PagerSnapHelperWithCallback
import kotlinx.android.synthetic.main.fragment_news.view.*

class NewsFragment : Fragment() {

    companion object {
        fun newInstance() = NewsFragment()
    }

    private lateinit var mViewModel: NewsViewModel
    private lateinit var mNewsNavigationRVAdapter: NewsNavigationRVAdapter
    private lateinit var mNewsDataRVAdapter: NewsDataRVAdapter
    private var mNewsNavigationList = mutableListOf<Drawable>()
    private var mNewsDataList = mutableListOf<NewsData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)

        context?.let {
            val ctx = it
            mNewsNavigationRVAdapter = NewsNavigationRVAdapter(it, mNewsNavigationList)
            mNewsDataRVAdapter = NewsDataRVAdapter(it, mNewsDataList)

            view?.news_navigation_rv?.let {
                it.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                it.adapter = mNewsNavigationRVAdapter

                val pagerSnapHelper = PagerSnapHelperWithCallback()
                pagerSnapHelper.attachToRecyclerView(it)
                view?.news_indicator?.setupWithPagerSnapHelper(pagerSnapHelper)
            }
            view?.news_data_rv?.let {
                it.layoutManager = LinearLayoutManager(ctx)
                it.adapter = mNewsDataRVAdapter
            }
        }

        init()
    }

    private fun init() {
        mViewModel.newsNavigation.observe(this, Observer {
            it?.let {
                mNewsNavigationList.clear()
                mNewsNavigationList.addAll(it)
                mNewsNavigationRVAdapter.notifyDataSetChanged()
                view?.news_indicator?.update(it.size)
            }
        })
        mViewModel.newsData.observe(this, Observer {
            it?.let {
                mNewsDataList.clear()
                mNewsDataList.addAll(it)
                mNewsDataRVAdapter.notifyDataSetChanged()
            }
        })
    }
}
