package io.github.phearing.phearing.common.widget

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import io.github.phearing.phearing.R
import kotlinx.android.synthetic.main.widget_loading_dialog.view.*

class LoadingDialog : DialogFragment() {
    companion object {
        fun newInstance() = LoadingDialog()
    }
    var dismissCallback: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.widget_loading_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.widget_loading_dialog_loading.start()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        view?.widget_loading_dialog_loading?.stop()
        dismissCallback?.invoke()
    }
}