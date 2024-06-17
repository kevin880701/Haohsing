package com.clockworkorange.haohsing.ui.main.user.analysis

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#008ED3")
        textSize = Utils.convertDpToPixel(12f)
    }

    override fun drawHighlighted(c: Canvas?, indices: Array<out Highlight>?) {
        val barData = mChart.barData

        for (high in indices!!) {
            val set = barData.getDataSetByIndex(high.dataSetIndex)
            if (set == null || !set.isHighlightEnabled) continue
            val e = set.getEntryForXValue(high.x, high.y)
            if (!isInBoundsX(e, set)) continue
            val trans = mChart.getTransformer(set.axisDependency)
            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.alpha = set.highLightAlpha
            val isStack = if (high.stackIndex >= 0 && e.isStacked) true else false
            val y1: Float
            val y2: Float
            if (isStack) {
                if (mChart.isHighlightFullBarEnabled) {
                    y1 = e.positiveSum
                    y2 = -e.negativeSum
                } else {
                    val range = e.ranges[high.stackIndex]
                    y1 = range.from
                    y2 = range.to
                }
            } else {
                y1 = e.y
                y2 = 0f
            }
            prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)
            setHighlightDrawPos(high, mBarRect)
            c?.drawRect(mBarRect, mHighlightPaint)
            c?.drawText(
                "%.0f".format(e.y),
                mBarRect.left - Utils.convertDpToPixel(3f),
                mBarRect.top - Utils.convertDpToPixel(3f),
                highlightPaint
            )
        }
    }
}