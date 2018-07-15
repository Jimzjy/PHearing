package io.github.phearing.phearing.ui.main

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.*
import kotlinx.android.synthetic.main.fragment_news.view.*

class NewsFragment : Fragment() {

    companion object {
        fun newInstance() = NewsFragment()
    }

    private lateinit var mViewModel: NewsViewModel
    private lateinit var mNewsNavigationRVAdapter: NewsNavigationRVAdapter
    private lateinit var mNewsDataRVAdapter: NewsDataRVAdapter
    private lateinit var mNewsDataLayoutManager: LinearLayoutManager
    private var mNewsNavigationList = mutableListOf<NavigationDataShow>()
    private var mNewsDataList = mutableListOf<NewsListDataShow>()
    private var mIsReady = false
    private var mImageLast = mutableListOf<String>()
    private val mImageBuf = mutableListOf<Drawable?>()

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
                mNewsDataLayoutManager = LinearLayoutManager(ctx)
                it.layoutManager = mNewsDataLayoutManager
                it.adapter = mNewsDataRVAdapter
            }
            view?.news_srl?.setColorSchemeColors(ContextCompat.getColor(ctx, R.color.colorPrimary))
        }

        init()
        view?.news_srl?.isRefreshing = true
        update()
    }

    override fun onStop() {
        super.onStop()
        mImageBuf.clear()
        if (view?.news_srl?.isRefreshing == true) {
            view?.news_srl?.isRefreshing = false
        }
    }

    private fun update() {
        mIsReady = true
        mImageBuf.clear()
        mViewModel.updateNews()
    }

    private fun init() {
        view?.news_search_card?.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }
        view?.news_srl?.setOnRefreshListener {
            mViewModel.isAddDataMode = false
            update()
        }
        view?.news_data_rv?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!mIsReady
                        && mNewsDataLayoutManager.findLastVisibleItemPosition() == mNewsDataRVAdapter.itemCount - 1
                        && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mIsReady = true
                    mViewModel.isAddDataMode = true
                    mViewModel.updateNextNews()
                }
            }
        })
        mNewsDataRVAdapter.setOnClickCallback {
            val intent = Intent(context, ContentActivity::class.java)
            intent.putExtra("url", it)
            startActivity(intent)
        }
        mNewsNavigationRVAdapter.setOnClickCallback {
            val intent = Intent(context, ContentActivity::class.java)
            intent.putExtra("url", it)
            startActivity(intent)
        }

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
        mViewModel.newsListDataList.observe(this, Observer {
            if (mIsReady) {
                if (it == null) {
                    view?.news_srl?.isRefreshing = false
                    mIsReady = false
                } else {
                    it.forEach {
                        if (it.image.isNotEmpty()) {
                            mImageLast.add(it.image)
                        }
                    }
                    if (mImageLast.size > 0) {
                        mViewModel.getNewsImage(mImageLast[0])
                    } else {
                        view?.news_srl?.isRefreshing = false
                        mIsReady = false
                    }
                    mViewModel.refreshNews(null)
                }
            }
        })
        mViewModel.newsListDataImage.observe(this, Observer {
            if (mIsReady && mImageLast.size > 0) {
                mImageBuf.add(it)
                mImageLast.removeAt(0)

                if (mImageLast.size > 0) {
                    mViewModel.getNewsImage(mImageLast[0])
                } else {
                    mViewModel.refreshNews(mImageBuf)
                    mImageBuf.clear()
                    mViewModel.updateNavigation()
                }
            }
        })
        mViewModel.newsNavigationDataList.observe(this, Observer {
            if (mIsReady) {
                if (it == null) {
                    view?.news_srl?.isRefreshing = false
                    mIsReady = false
                } else {
                    it.forEach {
                        if (it.image.isNotEmpty()) {
                            mImageLast.add(it.image)
                        }
                    }
                    if (mImageLast.size > 0) {
                        mViewModel.getNavigationImage(mImageLast[0])
                    } else {
                        view?.news_srl?.isRefreshing = false
                        mIsReady = false
                    }
                }
            }
        })
        mViewModel.newsNavigationDataImage.observe(this, Observer {
            if (mIsReady && mImageLast.size > 0) {
                mImageBuf.add(it)
                mImageLast.removeAt(0)

                if (mImageLast.size > 0) {
                    mViewModel.getNavigationImage(mImageLast[0])
                } else {
                    mIsReady = false
                    view?.news_srl?.isRefreshing = false
                    mViewModel.refreshNavigation(mImageBuf)
                }
            }
        })
    }
}
