package io.github.phearing.phearing.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.PagerSnapHelperWithCallback
import io.github.phearing.phearing.common.WidgetCardHintRVAdapter

class HintCard : FrameLayout {
    private lateinit var mRvAdapter: WidgetCardHintRVAdapter
    private lateinit var mIndicator: PointIndicator
    private var mContainerList = mutableListOf<View>()
    private var mCloseCallback: (() -> Unit)? = null

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        LayoutInflater.from(ctx).inflate(R.layout.widget_card_hint, this@HintCard)
        init()
    }

    private fun init() {
        val closeImageView = findViewById<ImageView>(R.id.widget_card_hint_close_iv)
        closeImageView.setOnClickListener {
            this@HintCard.visibility = View.INVISIBLE
            mCloseCallback?.invoke()
        }

        mRvAdapter = WidgetCardHintRVAdapter(context, mContainerList)
        val recyclerView = findViewById<RecyclerView>(R.id.widget_card_hint_rv)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = mRvAdapter
        val pagerSnapHelper = PagerSnapHelperWithCallback()
        pagerSnapHelper.attachToRecyclerView(recyclerView)

        mIndicator = findViewById(R.id.widget_card_hint_indicator)
        mIndicator.setupWithPagerSnapHelper(pagerSnapHelper)
    }

    fun setContainerList(containerList: List<View>, update: Boolean = false) {
        mContainerList.clear()
        mContainerList.addAll(containerList)
        if (update) update()
    }

    fun setCloseCallBack(callBack: () -> Unit) {
        mCloseCallback = callBack
    }

    fun update() {
        mRvAdapter.notifyDataSetChanged()
        mIndicator.update(mRvAdapter.itemCount)
    }
}