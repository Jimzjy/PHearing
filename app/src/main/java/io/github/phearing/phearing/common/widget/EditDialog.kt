package io.github.phearing.phearing.common.widget

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.github.phearing.phearing.R

const val EDIT_DIALOG_DATA = "edit_dialog_data"

open class EditDialog : DialogFragment() {
    companion object {
        fun newInstance(title: String) = EditDialog().apply {
            this.mTitleText = title
        }
    }

    private var mTitleText = ""

    private lateinit var mTitleET: TextInputEditText
    private lateinit var mTitleTL: TextInputLayout
    private lateinit var mCommitButton: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.widget_edit_dialog, container, false)

        mTitleET = view.findViewById(R.id.widget_edit_dialog_title_te)
        mTitleTL = view.findViewById(R.id.widget_edit_dialog_title_tl)
        mTitleET.hint = mTitleText
        mTitleET.setOnKeyListener { _, _, _ ->
            if (isTitleValid(mTitleET.text)) {
                mTitleTL.error = null
            }
            false
        }

        mCommitButton = view.findViewById(R.id.widget_edit_dialog_commit_bt)
        mCommitButton.setOnClickListener {
            if (isTitleValid(mTitleET.text)) {
                sendData(mTitleET.text.toString())
                dismiss()
            } else {
                mTitleTL.error = resources.getString(R.string.measure_error_hint)
            }
        }

        return view
    }

    private fun sendData(data: String) {
        val intent = Intent()
                .putExtra(EDIT_DIALOG_DATA, data)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    private fun isTitleValid(text: Editable?): Boolean {
        return text != null && text.isNotEmpty()
    }
}