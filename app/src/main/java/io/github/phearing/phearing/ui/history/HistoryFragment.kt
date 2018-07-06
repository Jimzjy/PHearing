package io.github.phearing.phearing.ui.history

import android.content.res.ColorStateList
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.HistoryRVAdapter
import io.github.phearing.phearing.common.ViewPagerViewAdapter
import io.github.phearing.phearing.room.audiometry.AudiometryData
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment() {
    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var mViewModel: HistoryViewModel
    private lateinit var mAudiometryRVAdapter: HistoryRVAdapter
    private lateinit var mSpeechRVAdapter: HistoryRVAdapter
    private val mAudiometryTimeList = mutableListOf<Long>()
    private val mAudiometryResultList = mutableListOf<String>()
    private val mSpeechTimeList = mutableListOf<Long>()
    private val mSpeechResultList = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)

        val viewPagerAdapter = ViewPagerViewAdapter()

        context?.let {
            mAudiometryRVAdapter = HistoryRVAdapter(it, mAudiometryTimeList, mAudiometryResultList)
            mSpeechRVAdapter = HistoryRVAdapter(it, mSpeechTimeList, mSpeechResultList)
            val audiometryDataRV = RecyclerView(it)
            val speechDataRV = RecyclerView(it)
            audiometryDataRV.adapter = mAudiometryRVAdapter
            speechDataRV.adapter = mSpeechRVAdapter
            audiometryDataRV.layoutManager = LinearLayoutManager(it)
            speechDataRV.layoutManager = LinearLayoutManager(it)

            viewPagerAdapter.addView(audiometryDataRV, resources.getString(R.string.audiometry))
            viewPagerAdapter.addView(speechDataRV, resources.getString(R.string.speech))
        }

        view?.let {
            it.history_view_pager.adapter = viewPagerAdapter
            it.history_tab_layout.setupWithViewPager(it.history_view_pager)
        }

        init()
    }

    private fun init() {
        mViewModel.audiometryData.observe(this, Observer {
            it?.let { setAudiometryDataList(it) }
        })

        mAudiometryRVAdapter.setOnClickCallback {
            val position = it
            mViewModel.audiometryData.value?.let {
                val pointList = it[position].rightData.split('|')
                val xPointList = it[position].leftData.split('|')
                val right = mutableListOf<Float>()
                val left = mutableListOf<Float>()

                pointList.forEach { right.add(it.toFloat()) }
                xPointList.forEach { left.add(it.toFloat()) }
                (activity as HistoryActivity).navigateTo(AMContentFragment.newInstance(right, left))
            }
        }
        mSpeechRVAdapter.setOnClickCallback {

        }
    }

    private fun setAudiometryDataList(data: List<AudiometryData>) {
        mAudiometryTimeList.clear()
        mAudiometryResultList.clear()
        data.forEach {
            mAudiometryTimeList.add(it.createTime)
            mAudiometryResultList.add("L:${it.leftResult} R:${it.rightResult}")
        }
        mAudiometryRVAdapter.notifyDataSetChanged()
    }
}
