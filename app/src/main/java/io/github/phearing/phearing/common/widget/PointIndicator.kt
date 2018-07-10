package io.github.phearing.phearing.common.widget

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.PagerSnapHelperWithCallback

class PointIndicator : View {
    private var mStep = 0f
    private var mRadius = 0f

    private var mMainColor = 0
    private var mOtherColor = 0
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mQuantity: Int = 0
    private var mMainPointState: Int = -1
    private val mFirstRect = RectF()

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        val ta = ctx.obtainStyledAttributes(attrs, R.styleable.PointIndicator, defStyleAttr, R.style.PointIndicator)

        mMainColor = ta.getColor(R.styleable.PointIndicator_indicatorMainPointColor, 0)
        mOtherColor = ta.getColor(R.styleable.PointIndicator_indicatorOtherPointColor, 0)
        mRadius = ta.getDimension(R.styleable.PointIndicator_indicatorPointRadius, 0f)

        ta.recycle()
        init()
    }

    private fun init() {
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.color = mOtherColor

        if (isInEditMode) {
            mQuantity = 3
            mMainPointState = 0
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = (mRadius * 4 * (mQuantity + 1)).toInt()
        val height = (mRadius * 4).toInt()

        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, height)
        } else if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, heightSize)
        } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mStep = w / (mQuantity + 1).toFloat()
        mFirstRect.left = mStep - mRadius
        mFirstRect.top = (h / 2f) - mRadius
        mFirstRect.right = mStep + mRadius
        mFirstRect.bottom = (h / 2f) + mRadius
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawOtherPoints(canvas)
            drawMainPoint(canvas)
        }
    }

    private fun drawOtherPoints(canvas: Canvas) {
        if (mQuantity <= 1) { return }
        canvas.drawOval(mFirstRect, mPaint)
        val rect = RectF(mFirstRect)
        for (i in 2..mQuantity) {
            rect.left += mStep
            rect.right += mStep
            canvas.drawOval(rect, mPaint)
        }
    }

    private fun drawMainPoint(canvas: Canvas) {
        if (mQuantity <= 1) { return }
        mPaint.color = mMainColor

        val rect = RectF(mFirstRect)
        rect.left += mMainPointState * mStep
        rect.right += mMainPointState * mStep
        canvas.drawOval(rect, mPaint)

        mPaint.color = mOtherColor
    }

    fun rePaint() {
        visibility = View.GONE
        invalidate()
        visibility = View.VISIBLE
    }

    fun setupWithViewPager(viewPager: ViewPager) {
        mQuantity = viewPager.adapter?.count ?: 0
        mMainPointState = viewPager.currentItem
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                mMainPointState = state
                rePaint()
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
        })
        rePaint()
    }

    fun setupWithPagerSnapHelper(helper: PagerSnapHelperWithCallback) {
        mMainPointState = 0
        helper.setFlingCallback { velocityX, _ ->
            when {
                velocityX > 0 -> {
                    if (mMainPointState + 1 < mQuantity) {
                        mMainPointState++
                    }
                }
                velocityX < 0 -> {
                    if (mMainPointState - 1 >= 0) {
                        mMainPointState--
                    }
                }
            }
            rePaint()
        }
        //rePaint()
    }

    fun update(quantity: Int) {
        mQuantity = quantity
        rePaint()
    }

//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    private class OvalOutLine(private val mRectFirst: Rect, private val mStep: Int, private val mQuantity: Int) : ViewOutlineProvider() {
//        override fun getOutline(p0: View?, p1: Outline?) {
//            val rect = Rect(mRectFirst)
//            p1?.let {
//                it.setOval(rect)
//                for (i in 2..mQuantity) {
//                    rect.left += mStep
//                    rect.right += mStep
//                    it.setOval(rect)
//                }
//            }
//        }
//    }
}