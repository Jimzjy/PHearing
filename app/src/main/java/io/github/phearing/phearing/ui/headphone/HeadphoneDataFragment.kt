package io.github.phearing.phearing.ui.headphone


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.WidgetListDialogRVAdapter
import io.github.phearing.phearing.room.headphone.Headphone
import kotlinx.android.synthetic.main.fragment_headphone_data.view.*


class HeadphoneDataFragment : Fragment() {
    companion object {
        fun newInstance() = HeadphoneDataFragment()
    }

    private lateinit var mViewModel: HeadphoneDataViewModel
    private lateinit var mRVAdapter: WidgetListDialogRVAdapter
    private var mHeadphoneList = mutableListOf<Headphone>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as HeadphoneActivity).supportActionBar?.title = resources.getString(R.string.headphone_data)
        return inflater.inflate(R.layout.fragment_headphone_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(HeadphoneDataViewModel::class.java)

        context?.let {
            val ctx = it
            mRVAdapter = WidgetListDialogRVAdapter(ctx, mHeadphoneList)
            view?.let {
                it.headphone_data_rv.layoutManager = LinearLayoutManager(ctx)
                it.headphone_data_rv.adapter = mRVAdapter
                it.headphone_data_rv.setHasFixedSize(true)

                it.headphone_data_fab.setOnClickListener {
                    (activity as HeadphoneActivity).navigateTo(MeasureFragment.newInstance())
                }
            }
        }
        init()
    }

    private fun init() {
        mViewModel.allHeadphones.observe(this, Observer {
            it?.let {
                mHeadphoneList.clear()
                mHeadphoneList.addAll(it)
                mRVAdapter.notifyDataSetChanged()
            }
        })
    }
}
