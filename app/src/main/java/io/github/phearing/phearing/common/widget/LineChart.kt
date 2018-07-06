package io.github.phearing.phearing.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import io.github.phearing.phearing.R
import kotlin.math.log2

const val OUT_LINE_STROKE_WIDTH = 3
const val LINE_STROKE_WIDTH = 10
const val POINT_STROKE_WIDTH = 25
const val WIDTH_DIV_LINE = 8f
const val HEIGHT_DIV_LINE = 13f
const val AXIS_TEXT = "dB HL / Hz"

class LineChart : View {
    private var mStepY = 0f
    private var mStepX = 0f

    private val mPaintOut = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintPoint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPathOut = Path()
    private val mPathOutDash = Path()
    private val mDashEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)

    private var mPointColor = 0
    private var mXPointColor = 0
    private var mPositionLineColor = 0
    private var mAxisTextColor = 0

//    private val mPointList = mutableListOf<Float>()
//    private val mXPointList = mutableListOf<Float>()
    var pointList = listOf<Float>()
    var xPointList = listOf<Float>()

    var minFrequency = 125
    var minDBHL = 0

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        val ta = ctx.obtainStyledAttributes(attrs, R.styleable.LineChart, defStyleAttr, R.style.LineChart)

        mPointColor = ta.getColor(R.styleable.LineChart_pointColor, 0)
        mXPointColor = ta.getColor(R.styleable.LineChart_xPointColor, 0)
        mPositionLineColor = ta.getColor(R.styleable.LineChart_positionLineColor, 0)
        mAxisTextColor = ta.getColor(R.styleable.LineChart_axisTextColor, 0)

        ta.recycle()

        init()
    }

    private fun init() {
        mPaintOut.color = mPositionLineColor
        mPaintOut.style = Paint.Style.STROKE
        mPaintOut.strokeWidth = OUT_LINE_STROKE_WIDTH.toFloat()
        mPaintOut.textAlign = Paint.Align.CENTER
        mPaintOut.isFakeBoldText = true

        mPaintPoint.color = mPointColor
        mPaintPoint.strokeWidth = POINT_STROKE_WIDTH.toFloat()
        mPaintPoint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mStepY = h / HEIGHT_DIV_LINE
        mStepX = w / WIDTH_DIV_LINE

        changeOutLinePath()
        mPaintOut.textSize = (mStepY + mStepX) / 7f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawOutLine(it)
            drawAxisText(it)
            drawPoint(it)
        }
    }

    private fun changeOutLinePath() {
        var left: Float = mStepX
        val right: Float = mStepX * (WIDTH_DIV_LINE - 1)
        var top = mStepY
        val end = mStepY * (HEIGHT_DIV_LINE - 1)

        mPathOut.reset()
        mPathOutDash.reset()
        for (i in 1..12) {
            mPathOut.moveTo(left, top)
            mPathOut.lineTo(right, top)
            top += mStepY
        }

        top = mStepY
        for (i in 1..7) {
            mPathOut.moveTo(left, top)
            mPathOut.lineTo(left, end)
            left += mStepX
        }

        left = mStepX * 3.5f
        for (i in 1..4) {
            mPathOutDash.moveTo(left, top)
            mPathOutDash.lineTo(left, end)
            left += mStepX
        }
    }

    private fun drawOutLine(canvas: Canvas) {
        canvas.drawPath(mPathOut, mPaintOut)
        mPaintOut.pathEffect = mDashEffect
        canvas.drawPath(mPathOutDash, mPaintOut)
        mPaintOut.pathEffect = null
    }

    private fun drawAxisText(canvas: Canvas) {
        mPaintOut.color = mAxisTextColor
        mPaintOut.style = Paint.Style.FILL
        val fontSpacing = mPaintOut.fontSpacing / 2f

        var freq = minFrequency
        var db = minDBHL

        var x = mStepX / 2
        var y = mStepY + fontSpacing

        for (i in 1..12) {
            canvas.drawText(db.toString(), x, y, mPaintOut)
            db += 10
            y += mStepY
        }

        x = mStepX
        y = mStepY / 2 + fontSpacing
        for (i in 1..7) {
            canvas.drawText(freq.toString(), x, y, mPaintOut)
            freq *= 2
            x += mStepX
        }

        x = mStepX * 3.5f
        y = mStepY * (HEIGHT_DIV_LINE - 0.65f) + fontSpacing
        freq = minFrequency * 6
        for (i in 1..4) {
            canvas.drawText(freq.toString(), x, y, mPaintOut)
            freq *= 2
            x += mStepX
        }

        canvas.drawText(AXIS_TEXT, mStepX * 2, y, mPaintOut)

        mPaintOut.color = mPositionLineColor
        mPaintOut.style = Paint.Style.STROKE
    }

    private fun drawPoint(canvas: Canvas) {
        val pointArray = parsePointList(pointList)
        val xPointArray = parsePointList(xPointList)

        if (pointArray.isNotEmpty()) {
            canvas.drawPoints(pointArray, mPaintPoint)

            mPaintPoint.strokeWidth = LINE_STROKE_WIDTH.toFloat()
            canvas.drawLines(pointArray, mPaintPoint)
            mPaintPoint.strokeWidth = POINT_STROKE_WIDTH.toFloat()
        }
        if (xPointArray.isNotEmpty()) {
            mPaintPoint.color = mXPointColor
            canvas.drawPoints(xPointArray, mPaintPoint)

            mPaintPoint.strokeWidth = LINE_STROKE_WIDTH.toFloat()
            canvas.drawLines(xPointArray, mPaintPoint)
            mPaintPoint.strokeWidth = POINT_STROKE_WIDTH.toFloat()

            mPaintPoint.color = mPointColor
        }
    }

    private fun parsePointList(pointList: List<Float>): FloatArray {
        val list = mutableListOf<Float>()
        val tmpPointList = mutableListOf<Float>()

        var count = 0
        while (count < pointList.size) {
            tmpPointList.add(log2(pointList[count] / minFrequency) * mStepX + mStepX)
            tmpPointList.add((pointList[count+1] / 10f) * mStepY + mStepY)
            count += 2
        }

        if (pointList.size > 4) {
            list.add(tmpPointList[0])
            list.add(tmpPointList[1])
            var i = 2
            while (i < tmpPointList.size) {
                list.add(tmpPointList[i])
                list.add(tmpPointList[i+1])
                list.add(tmpPointList[i])
                list.add(tmpPointList[i+1])
                i += 2
            }
        } else {
            tmpPointList.forEach {
                list.add(it)
            }
        }
        return list.toFloatArray()
    }

    fun rePaint() {
        invalidate()
    }
}