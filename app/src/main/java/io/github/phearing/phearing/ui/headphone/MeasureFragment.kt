package io.github.phearing.phearing.ui.headphone

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.widget.CurveChart
import io.github.phearing.phearing.common.widget.EDIT_DIALOG_DATA
import io.github.phearing.phearing.common.widget.EditDialog
import io.github.phearing.phearing.databinding.FragmentMeasureBinding
import kotlinx.android.synthetic.main.hint_measure0.view.*

const val MAX_LINE_COLOR_RIGHT = "#E91E63"
const val MIN_LINE_COLOR_RIGHT = "#2196F3"
const val MAX_LINE_COLOR_LEFT = "#FFC107"
const val MIN_LINE_COLOR_LEFT = "#5E35B1"
const val MEASURE_ADD_NAME = 0

class MeasureFragment : Fragment() {
    companion object {
        fun newInstance() = MeasureFragment()
    }

    private lateinit var mViewModel: MeasureViewModel
    private lateinit var mBinding: FragmentMeasureBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as HeadphoneActivity).supportActionBar?.title = resources.getString(R.string.measure_headphone)
        mBinding = FragmentMeasureBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(MeasureViewModel::class.java)
        mBinding.viewModel = mViewModel
        mBinding.setLifecycleOwner(this@MeasureFragment)
        init()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            MEASURE_ADD_NAME -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        val name = it.getStringExtra(EDIT_DIALOG_DATA)
                        if (mViewModel.insertHeadphoneData(name)) {
                            Toast.makeText(context,
                                    resources.getString(R.string.measure_add_headphone_successful), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context,
                                    resources.getString(R.string.measure_add_headphone_error), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun init() {
        mBinding.measureCurveChart.let {
            it.maxY = 70f
            it.maxX = 8000f
            it.yText = "dB HL"
            it.xText = "Hz"
        }
        mBinding.measureDataCv.setOnClickListener {
            mViewModel.isHintOpen.value?.let {
                mViewModel.isHintOpen.value = !it
            }
        }
        mBinding.measureHintCard.setCloseCallBack {
            mViewModel.isHintOpen.value = false
        }

        mViewModel.state.observe(this, Observer {
            it?.let {
                mBinding.measureHintCard.setContainerList(getHintCardContent(it), true)
            }
        })
        mViewModel.isHintOpen.observe(this, Observer {
            it?.let {
                if (it) {
                    mBinding.measureHintCard.visibility = View.VISIBLE
                    mBinding.measureChartCv.visibility = View.INVISIBLE
                } else {
                    mBinding.measureHintCard.visibility = View.INVISIBLE
                    mBinding.measureChartCv.visibility = View.VISIBLE
                }
            }
        })
        mViewModel.minDbListRight.observe(this, Observer {
            it?.let { mBinding.measureCurveChart
                    .updateLine(0, it, Color.parseColor(MIN_LINE_COLOR_RIGHT)) }
        })
        mViewModel.maxDbListRight.observe(this, Observer {
            it?.let { mBinding.measureCurveChart
                    .updateLine(1, it, Color.parseColor(MAX_LINE_COLOR_RIGHT), true) }
        })
        mViewModel.minDbListLeft.observe(this, Observer {
            it?.let { mBinding.measureCurveChart
                    .updateLine(2, it, Color.parseColor(MIN_LINE_COLOR_LEFT)) }
        })
        mViewModel.maxDbListLeft.observe(this, Observer {
            it?.let { mBinding.measureCurveChart
                    .updateLine(3, it, Color.parseColor(MAX_LINE_COLOR_LEFT), true) }
        })
    }

    private fun getHintCardContent(state: Int): List<View> {
        val list = mutableListOf<View>()
        return when(state) {
            MEASURE_START -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_measure4)
                val stopBT = view0.findViewById<MaterialButton>(R.id.hint_measure4_bt)
                stopBT.setOnClickListener { mViewModel.stop() }
                list.add(view0)
                list
            }
            MEASURE_PREPARE -> {
                val view0 = getView<ScrollView>(R.layout.hint_measure0)
                val view1 = getView<ConstraintLayout>(R.layout.hint_measure1)

                val startBT = view0.findViewById<MaterialButton>(R.id.hint_measure0_bt)
                startBT.setOnClickListener {
                    view0.hint_measure0_et.text?.let {
                        if (it.isNotEmpty()) {
                            mViewModel.micOffset = it.toString().toInt()
                        }
                    }
                    mViewModel.startMeasure()
                }
                val curveChart = view1.findViewById<CurveChart>(R.id.hint_measure1_curve_chart)
                curveChart.maxY = 10f
                curveChart.maxX = 10f
                curveChart.yText = "dB HL"
                curveChart.xText = "Hz"
                curveChart.updateLine(0, floatArrayOf(1f,3f,3f,5f,5f,6f,7f,6.1f,9f,6.5f),
                        Color.parseColor(MAX_LINE_COLOR_LEFT))
                curveChart.updateLine(1, floatArrayOf(1f,1f,3f,2f,5f,4f,7f,4.1f,9f,4.2f),
                        Color.parseColor(MIN_LINE_COLOR_LEFT))
                list.add(view0)
                list.add(view1)
                list
            }
            MEASURE_CONTINUE -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_measure2)
                val continueBT = view0.findViewById<MaterialButton>(R.id.hint_measure2_bt)
                continueBT.setOnClickListener { mViewModel.startMeasure() }
                list.add(view0)
                list
            }
            MEASURE_FINISH -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_measure3)
                val addBT = view0.findViewById<ImageView>(R.id.hint_measure3_iv)
                addBT.setOnClickListener {
                    val editDialog = EditDialog.newInstance(resources.getString(R.string.measure_room_name))
                    editDialog.setTargetFragment(this@MeasureFragment, MEASURE_ADD_NAME)
                    editDialog.show(fragmentManager, "measure_editDialog")
                }
                val startBT = view0.findViewById<MaterialButton>(R.id.hint_measure3_bt)
                startBT.setOnClickListener { mViewModel.startMeasure() }
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
}
