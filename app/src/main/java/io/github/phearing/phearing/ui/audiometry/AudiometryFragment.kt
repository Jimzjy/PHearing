package io.github.phearing.phearing.ui.audiometry

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.widget.LIST_DIALOG_DATA
import io.github.phearing.phearing.common.widget.LineChart
import io.github.phearing.phearing.common.widget.ListDialog
import io.github.phearing.phearing.databinding.FragmentAudiometryBinding
import io.github.phearing.phearing.room.headphone.Headphone

const val AUDIOMETRY_GET_HEADPHONE = 0

class AudiometryFragment : Fragment() {
    companion object {
        fun newInstance() = AudiometryFragment()
    }

    private lateinit var mViewModel: AudiometryViewModel
    private lateinit var mBinding: FragmentAudiometryBinding
    private var mHeadphoneList: List<Headphone>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentAudiometryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(AudiometryViewModel::class.java)
        mBinding.viewModel = mViewModel
        mBinding.setLifecycleOwner(this@AudiometryFragment)
        init()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            AUDIOMETRY_GET_HEADPHONE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        val index = it.getIntExtra(LIST_DIALOG_DATA, -1)
                        if (index >= 0) {
                            mHeadphoneList?.let {
                                mViewModel.headphone.value = it[index]
                                mViewModel.startTest()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun init() {
        mBinding.audiometryUnheard.setOnClickListener {
            mViewModel.unHeard()
        }
        mBinding.audiometryHeard.setOnClickListener {
            mViewModel.heard()
        }
        mBinding.audiometryReplay.setOnClickListener {
            mViewModel.play()
        }
        mBinding.audiometryDataCard.setOnClickListener {
            mViewModel.isHintOpen.value?.let {
                mViewModel.isHintOpen.value = !it
            }
        }
        mBinding.audiometryHintCard.setCloseCallBack {
            mViewModel.isHintOpen.value = false
        }

        mViewModel.state.observe(this, Observer {
            it?.let {
                when(it) {
                    AUDIOMETRY_START -> {
                        mBinding.audiometryControlPanel.visibility = View.VISIBLE
                    }
                    AUDIOMETRY_PREPARE -> {
                        mBinding.audiometryControlPanel.visibility = View.GONE
                    }
                    AUDIOMETRY_FINISH -> {
                        mBinding.audiometryControlPanel.visibility = View.GONE
                    }
                }
                mBinding.audiometryHintCard.setContainerList(getHintCardContent(it), true)
            }
        })
        mViewModel.isHintOpen.observe(this, Observer {
            if (it == true) {
                mBinding.audiometryHintCard.visibility = View.VISIBLE
                mBinding.audiometryChartCard.visibility = View.INVISIBLE
            } else {
                mBinding.audiometryHintCard.visibility = View.INVISIBLE
                mBinding.audiometryChartCard.visibility = View.VISIBLE
            }
        })
        mViewModel.pointList.observe(this, Observer {
            it?.let {
                mBinding.audiometryLineChart.pointList = it
                mBinding.audiometryLineChart.rePaint()
            }
        })
        mViewModel.xPointList.observe(this, Observer {
            it?.let {
                mBinding.audiometryLineChart.xPointList = it
                mBinding.audiometryLineChart.rePaint()
            }
        })
        mViewModel.allHeadphones.observe(this, Observer {
            it?.let {
                mHeadphoneList = it
            }
        })
        mViewModel.startPlayAnimation.observe(this, Observer {
            it?.let {
                if (it > 0) {
                    mBinding.audiometryReplay.rotation = 0f
                    mBinding.audiometryReplay.animate()
                            .setDuration(DEFAULT_DURATION.toLong())
                            .rotationBy(-360f)
                }
            }
        })
        mViewModel.headphone.observe(this, Observer {
            it?.let {
                mViewModel.rightVolumeDBHL.clear()
                mViewModel.leftVolumeDBHL.clear()
                val right = it.rightVolume.split('|')
                val left = it.leftVolume.split('|')

                for (i in 0 until 7) {
                    val list = mutableListOf<Float>()
                    right.subList(i, i + 15).forEach {
                        list.add(it.toFloat())
                    }
                    mViewModel.rightVolumeDBHL.add(list.toFloatArray())
                    for (i0 in 0 until list.size) {
                        if (list[i0] >= 0) {
                            mViewModel.minMaxRightDBHL[0].add(i0 * 5)
                        }
                    }
                    for (i0 in (list.size - 1) downTo 0) {
                        if (list[i0] >= 0) {
                            mViewModel.minMaxRightDBHL[1].add(i0 * 5)
                        }
                    }

                    list.clear()
                    left.subList(i, i + 15).forEach {
                        list.add(it.toFloat())
                    }
                    mViewModel.leftVolumeDBHL.add(list.toFloatArray())
                    for (i0 in 0 until list.size) {
                        if (list[i0] >= 0) {
                            mViewModel.minMaxLeftDBHL[0].add(i0 * 5)
                        }
                    }
                    for (i0 in (list.size - 1) downTo 0) {
                        if (list[i0] >= 0) {
                            mViewModel.minMaxLeftDBHL[1].add(i0 * 5)
                        }
                    }
                }
            }
        })
    }

    private fun getHintCardContent(state: Int): List<View> {
        val list = mutableListOf<View>()
        return when(state) {
            AUDIOMETRY_START -> {
                list
            }
            AUDIOMETRY_PREPARE -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_audiometry0)
                val startBT = view0.findViewById<MaterialButton>(R.id.hint_audiometry0_bt)
                startBT.setOnClickListener {
                    if (mViewModel.headphone.value != null) {
                        mViewModel.startTest()
                    } else {
                        val listDialog = ListDialog
                                .newInstance(
                                        resources.getString(R.string.select_headphone_from_below),
                                        mHeadphoneList ?: emptyList())
                        listDialog.setTargetFragment(this@AudiometryFragment,
                                AUDIOMETRY_GET_HEADPHONE)
                        listDialog.show(fragmentManager, "audiometry_listDialog")
                    }
                }
                val view1 = getView<ConstraintLayout>(R.layout.hint_audiometry1)

                val view2 = getView<ScrollView>(R.layout.hint_audiometry3)
                val chart = view2.findViewById<LineChart>(R.id.hint_audiometry3_chart)
                chart.pointList = listOf(125f, 5f, 250f, 10f, 500f, 10f, 1000f, 20f, 2000f, 25f, 4000f, 25f, 8000f, 20f)
                chart.xPointList = listOf(125f, 10f, 250f, 15f, 500f, 20f, 1000f, 25f, 2000f, 25f, 4000f, 30f, 8000f, 25f)

                list.add(view0)
                list.add(view1)
                list.add(view2)
                list
            }
            AUDIOMETRY_FINISH -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_audiometry2)
                val startBT = view0.findViewById<MaterialButton>(R.id.hint_audiometry2_bt)
                startBT.setOnClickListener {
                    if (mViewModel.headphone.value != null) {
                        mViewModel.startTest()
                    } else {
                        val listDialog = ListDialog
                                .newInstance(
                                        resources.getString(R.string.select_headphone_from_below),
                                        mHeadphoneList ?: emptyList())
                        listDialog.setTargetFragment(this@AudiometryFragment,
                                AUDIOMETRY_GET_HEADPHONE)
                        listDialog.show(fragmentManager, "audiometry_listDialog")
                    }
                }
                val resultTv = view0.findViewById<TextView>(R.id.hint_audiometry2_result_tv)
                resultTv.text = getFormatString()

                list.add(view0)
                list
            }
            else -> {
                list
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: View> getView(resId: Int): T {
        return layoutInflater.inflate(resId, null, false) as T
    }

    private fun getFormatString(): String {
        val level = arrayOf(
                resources.getString(R.string.audiometry_level_normal),
                resources.getString(R.string.audiometry_level_light),
                resources.getString(R.string.audiometry_level_medium),
                resources.getString(R.string.audiometry_level_medium_heavy)
        )
        return String.format(resources.getString(R.string.audiometry_result),
                mViewModel.rightLevel,
                level[(mViewModel.rightLevel / 25).toInt()],
                mViewModel.leftLevel,
                level[(mViewModel.leftLevel / 25).toInt()])
    }

}
