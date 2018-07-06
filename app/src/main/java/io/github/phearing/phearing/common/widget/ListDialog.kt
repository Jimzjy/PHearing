package io.github.phearing.phearing.common.widget

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.WidgetListDialogRVAdapter
import io.github.phearing.phearing.room.headphone.Headphone

const val LIST_DIALOG_DATA = "list_dialog_data"

class ListDialog : DialogFragment() {
    companion object {
        fun newInstance(title: String, headphoneList: List<Headphone>) = ListDialog().apply {
            this.mTitleText = title
            this.mHeadphoneList = headphoneList
        }
    }

    private var mTitleText = ""
    private var mHeadphoneList: List<Headphone>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.widget_list_dialog, container, false)

        val titleTv = view.findViewById<TextView>(R.id.widget_list_dialog_title_tv)
        titleTv.text = mTitleText

        context?.let {
            val recyclerView = view.findViewById<RecyclerView>(R.id.widget_list_dialog_rv)
            val rvAdapter = WidgetListDialogRVAdapter(it, mHeadphoneList ?: emptyList())
            recyclerView.layoutManager = LinearLayoutManager(it)
            recyclerView.adapter = rvAdapter
            rvAdapter.setOnClickCallback {
                sendData(it)
                dismiss()
            }
        }

        return view
    }

    private fun sendData(data: Int) {
        val intent = Intent()
                .putExtra(LIST_DIALOG_DATA, data)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }
}