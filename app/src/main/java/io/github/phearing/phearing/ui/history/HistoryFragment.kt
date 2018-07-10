package io.github.phearing.phearing.ui.history


import android.accessibilityservice.AccessibilityService
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.HistoryRVAdapter
import io.github.phearing.phearing.common.ViewPagerViewAdapter
import io.github.phearing.phearing.room.audiometry.AudiometryData
import io.github.phearing.phearing.room.speech.SpeechData
import kotlinx.android.synthetic.main.fragment_history.view.*

const val AUDIOMETRY_HISTORY = 0
const val SPEECH_HISTORY = 1

class HistoryFragment : Fragment() {
    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var mViewModel: HistoryViewModel
    private lateinit var mAudiometryRVAdapter: HistoryRVAdapter
    private lateinit var mSpeechRVAdapter: HistoryRVAdapter
    private lateinit var mAudiometryRv: RecyclerView
    private lateinit var mSpeechRv: RecyclerView
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
            mAudiometryRv = RecyclerView(it)
            mSpeechRv = RecyclerView(it)
            mAudiometryRv.adapter = mAudiometryRVAdapter
            mSpeechRv.adapter = mSpeechRVAdapter
            mAudiometryRv.layoutManager = LinearLayoutManager(it)
            mSpeechRv.layoutManager = LinearLayoutManager(it)

            viewPagerAdapter.addView(mAudiometryRv, resources.getString(R.string.audiometry))
            viewPagerAdapter.addView(mSpeechRv, resources.getString(R.string.speech))
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
        mViewModel.speechData.observe(this, Observer {
            it?.let { setSpeechData(it) }
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
        mAudiometryRVAdapter.setOnLongClickCallback {
            showPopupMenu(it, AUDIOMETRY_HISTORY)
        }

        mSpeechRVAdapter.setOnLongClickCallback {
            showPopupMenu(it, SPEECH_HISTORY)
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

    private fun setSpeechData(data: List<SpeechData>) {
        mSpeechTimeList.clear()
        mSpeechResultList.clear()

        val scoreText = resources.getString(R.string.score)
        val tableText = resources.getString(R.string.test_table)
        data.forEach {
            mSpeechTimeList.add(it.createTime)
            mSpeechResultList.add("$scoreText${it.score}  $tableText${it.tableNo}")
        }
        mSpeechRVAdapter.notifyDataSetChanged()
    }

    private fun showPopupMenu(position: Int, flag: Int) {
        context?.let {
            val popupMenu: PopupMenu
            val wrapper = ContextThemeWrapper(it, R.style.Widget_PHearing_PopupMenu)

            when(flag) {
                AUDIOMETRY_HISTORY -> {
                    popupMenu = PopupMenu(wrapper, mAudiometryRv.getChildAt(position))
                    popupMenu.menuInflater.inflate(R.menu.menu_history_auiometry, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener {
                        when(it.itemId) {
                            R.id.menu_history_audiometry_delete -> {
                                mViewModel.deleteAudiometryData(position)
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
                SPEECH_HISTORY -> {
                    popupMenu = PopupMenu(wrapper, mSpeechRv.getChildAt(position))
                    popupMenu.menuInflater.inflate(R.menu.menu_history_speech, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener {
                        when(it.itemId) {
                            R.id.menu_history_speech_delete -> {
                                mViewModel.deleteSpeechDate(position)
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
            }
        }
    }
}
