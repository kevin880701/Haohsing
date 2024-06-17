package com.clockworkorange.haohsing.ui.main.user.analysis

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomLineChartRenderer(
    chart: LineDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineChartRenderer(chart, animator, viewPortHandler) {

    private val highlightColor = Color.parseColor("#F5C260")

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = highlightColor
        textSize = Utils.convertDpToPixel(14f)
    }

    override fun drawHighlighted(c: Canvas?, indices: Array<out Highlight>?) {
        val lineData = mChart.lineData

        for (high in indices!!) {
            val set = lineData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y)

            if (!isInBoundsX(e, set)) continue

            val pix = mChart.getTransformer(set.axisDependency)
                .getPixelForValues(e.x, e.y * mAnimator.phaseY)

            high.setDraw(pix.x.toFloat(), pix.y.toFloat())

            // draw the lines
            drawHighlightLines(c, pix.x.toFloat(), pix.y.toFloat(), set)

            highlightPaint.color = Color.WHITE

            c?.drawCircle(
                pix.x.toFloat(),
                pix.y.toFloat(),
                Utils.convertDpToPixel(4f),
                highlightPaint
            )
            highlightPaint.color = highlightColor
            c?.drawCircle(
                pix.x.toFloat(),
                pix.y.toFloat(),
                Utils.convertDpToPixel(3f),
                highlightPaint
            )

            c?.drawText(
                "%.0f".format(e.y),
                pix.x.toFloat() - Utils.convertDpToPixel(7f),
                pix.y.toFloat() - Utils.convertDpToPixel(4f),
                highlightPaint
            )
        }

    }
}