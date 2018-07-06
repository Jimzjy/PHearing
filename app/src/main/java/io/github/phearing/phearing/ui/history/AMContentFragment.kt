package io.github.phearing.phearing.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.github.phearing.phearing.R
import kotlinx.android.synthetic.main.fragment_amcontent.view.*

const val POINT_LIST = "point"
const val X_POINT_LIST = "xPoint"

class AMContentFragment : Fragment() {
    companion object {
        fun newInstance(pointList: List<Float>, xPointList: List<Float>) = AMContentFragment().apply {
            this.mPointList = pointList
            this.mXPointList = xPointList
        }
    }

    private lateinit var mPointList: List<Float>
    private lateinit var mXPointList: List<Float>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            mPointList = it.getFloatArray(POINT_LIST).toList()
            mXPointList = it.getFloatArray(X_POINT_LIST).toList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_amcontent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.history_content_line_chart.pointList = mPointList
        view.history_content_line_chart.xPointList = mXPointList
        view.history_content_line_chart.rePaint()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putFloatArray(POINT_LIST, mPointList.toFloatArray())
        outState.putFloatArray(X_POINT_LIST, mXPointList.toFloatArray())
    }
}
