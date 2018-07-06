package io.github.phearing.phearing.common

import androidx.recyclerview.widget.PagerSnapHelper

class PagerSnapHelperWithCallback : PagerSnapHelper() {
    private var mFlingCallback: ((velocityX: Int, velocityY: Int) -> Unit)? = null

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
        return if (super.onFling(velocityX, velocityY)) {
            mFlingCallback?.invoke(velocityX, velocityY)
            true
        } else {
            false
        }
    }

    fun setFlingCallback(callback: (velocityX: Int, velocityY: Int) -> Unit) {
        mFlingCallback = callback
    }
}