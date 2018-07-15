package io.github.phearing.phearing.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import br.tiagohm.markdownview.css.styles.Github
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.widget.LoadingDialog
import kotlinx.android.synthetic.main.fragment_content.view.*

class ContentFragment : Fragment() {
    companion object {
        fun newInstance() = ContentFragment()

        fun newInstance(url: String) = ContentFragment().apply {
            this.mUrl = url
        }
    }

    private lateinit var mViewModel: ContentViewModel
    private var mUrl = ""
    private var mIsReady = false
    private var mLoadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ContentViewModel::class.java)
        if (mUrl.isNotEmpty()) mViewModel.url = mUrl

        init()

    }

    override fun onResume() {
        super.onResume()
        mLoadingDialog = LoadingDialog.newInstance()
        mLoadingDialog?.isCancelable = false
        mLoadingDialog?.show(fragmentManager, "content_loadingDialog")
        update()
    }

    private fun init() {
        view?.content_content_v?.addStyleSheet(Github())

        mViewModel.newsData.observe(this, Observer {
            if (mIsReady) {
                mIsReady = false
                mLoadingDialog?.dismiss()
                if (it == null) {

                } else {
                    view?.content_title_tv?.text = it.title
                    view?.content_content_v?.loadMarkdown(it.content)
                    view?.content_time_tv?.text = it.pubTime
                }
            }
        })
    }

    private fun update() {
        mIsReady = true
        mViewModel.getNews()
    }
}
