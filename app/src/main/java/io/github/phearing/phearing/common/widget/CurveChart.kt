package io.github.phearing.phearing.common.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.phearing.phearing.R

const val WIDTH_DIVIDE_CURVE = 10
const val HEIGHT_DIVIDE_CURVE = 10

class CurveChart : View {
    private var mWidth = 0f
    private var mHeight = 0f

    private var mPositionLineColor = 0
    private var mAxisTextColor = 0
    private val mLineColorList = mutableListOf<Int>()
    private val mLinePathList = mutableListOf<Path>()
    private val mPositionList = mutableListOf<FloatArray>()

    private val mPaintOut = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintLine = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPathOut = Path()

    var outPaintWidth = 3.5f
        set(value) { mPaintOut.strokeWidth = value; field = value }
    var linePaintWidth = 3.5f
        set(value) { mPaintLine.strokeWidth = value; field = value }

    var yText = ""
    var xText = ""
    var maxY = 0f
    var maxX = 0f

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        val ta = ctx.obtainStyledAttributes(attrs, R.styleable.CurveChart, defStyleAttr, R.style.CurveChart)

        mPositionLineColor = ta.getColor(R.styleable.CurveChart_c_positionLineColor, 0)
        mAxisTextColor = ta.getColor(R.styleable.CurveChart_c_axisTextColor, 0)

        ta.recycle()
        init()
    }

    private fun init() {
        mPaintOut.color = mPositionLineColor
        mPaintOut.strokeWidth = outPaintWidth
        mPaintOut.style = Paint.Style.STROKE

        mPaintLine.pathEffect = CornerPathEffect(100f)
        mPaintLine.style = Paint.Style.STROKE
        mPaintLine.strokeWidth = linePaintWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = 800
        val height = 1000

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

        mWidth = w.toFloat()
        mHeight = h.toFloat()

        changeOutPath()
        mPaintOut.textSize = (mWidth + mHeight) / 70f
        changeLinePath()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawOutLine(it)
            drawAxisText(it)
            drawLine(it)
        }
    }

    fun addLine(positions: FloatArray, color: Int, rePaint: Boolean = false) {
        mLineColorList.add(color)
        mPositionList.add(processPositions(positions))
        updatePath(mPositionList.last())

        if (rePaint) rePaint()
    }

    fun updateLine(index: Int, positions: FloatArray, color: Int, rePaint: Boolean = false) {
        if (mPositionList.size >= index + 1) {
            mLineColorList[index] = color
            mPositionList[index] = processPositions(positions)
            updatePath(mPositionList[index], index)
        } else {
            addLine(positions, color, rePaint)
        }
    }

    fun rePaint() {
        invalidate()
    }

    private fun changeLinePath() {
        mLinePathList.clear()

        mPositionList.forEach {
            updatePath(it)
        }
    }

    private fun toPosition(x: Float, y: Float): Array<Float> {
        // -1f in headphone: unvalidated
        if (y == -1f) {
            return emptyArray()
        }
        val stepX = mWidth / WIDTH_DIVIDE_CURVE.toFloat()
        val stepY = mHeight / HEIGHT_DIVIDE_CURVE.toFloat()

        val xP = x / maxX * (mWidth - stepX * 2)  + stepX
        val yP = mHeight - stepY - y / maxY * (mHeight - stepY * 2)

        return arrayOf(xP, yP)
    }

    private fun processPositions(positions: FloatArray): FloatArray {
        val list = mutableListOf<Float>()
        if (positions.size <= 4) {
            positions.forEach { list.add(it) }
        } else {
            list.add(positions[0])
            list.add(positions[1])
            var i = 0
            while (i < positions.size) {
                list.add(positions[i])
                list.add(positions[i+1])
                i += 2
            }
        }
        return list.toFloatArray()
    }

    /**
     * @param positions processed positions [mPositionList]
     */
    private fun updatePath(positions: FloatArray, index: Int = -1) {
        var i = 0
        val list = mutableListOf<Float>()
        while (i < positions.size) {
            val p = toPosition(positions[i], positions[i+1])
            p.forEach {
                list.add(it)
            }
            i += 2
        }

        val path = Path()
        path.moveTo(list[0], list[1])
        i = 2
        while (i < list.size) {
            path.lineTo(list[i], list[i+1])
            i += 2
        }
        if (index >= 0) {
            mLinePathList[index] = path
        } else {
            mLinePathList.add(path)
        }
    }

    private fun drawLine(canvas: Canvas) {
        for (i in 0 until mLinePathList.size) {
            mPaintLine.color = mLineColorList[i]
            canvas.drawPath(mLinePathList[i], mPaintLine)
        }
    }

    private fun changeOutPath() {
        val stepX = mWidth / WIDTH_DIVIDE_CURVE.toFloat()
        val stepY = mHeight / HEIGHT_DIVIDE_CURVE.toFloat()
        val ratio = 0.1f
        mPathOut.reset()

        mPathOut.moveTo(stepX, stepY)
        mPathOut.lineTo(stepX, mHeight - stepY)
        mPathOut.lineTo(mWidth - stepX, mHeight - stepY)
        mPathOut.moveTo(mWidth - stepX * (1 + ratio), mHeight - stepY * (1 + ratio))
        mPathOut.lineTo(mWidth - stepX, mHeight - stepY)
        mPathOut.lineTo(mWidth - stepX * (1 + ratio), mHeight - stepY * (1 - ratio))
        mPathOut.moveTo(stepX * (1 - ratio), stepY * (1 + ratio))
        mPathOut.lineTo(stepX, stepY)
        mPathOut.lineTo(stepX * (1 + ratio), stepY * (1 + ratio))
    }

    private fun drawOutLine(canvas: Canvas) {
        canvas.drawPath(mPathOut, mPaintOut)
    }

    private fun drawAxisText(canvas: Canvas) {
        mPaintOut.color = mAxisTextColor
        mPaintOut.style = Paint.Style.FILL

        val stepX = mWidth / WIDTH_DIVIDE_CURVE.toFloat()
        val stepY = mHeight / HEIGHT_DIVIDE_CURVE.toFloat()
        val ratio = 0.5f

        canvas.drawText(yText, stepX * (1 + ratio), stepY, mPaintOut)
        canvas.drawText(xText, mWidth - stepX * (1 + ratio), mHeight - stepY * (1 - ratio), mPaintOut)

        mPaintOut.color = mPositionLineColor
        mPaintOut.style = Paint.Style.STROKE
    }
}