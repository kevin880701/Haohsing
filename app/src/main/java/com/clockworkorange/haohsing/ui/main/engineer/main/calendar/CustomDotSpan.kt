package com.clockworkorange.haohsing.ui.main.engineer.main.calendar

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.LineBackgroundSpan

class CustomDotSpan(private val dotCount: DotCount = DotCount.None) : LineBackgroundSpan {

    enum class DotCount {
        None, One, Two, More
    }

    private var radius = 8f
    private val dotColor = Color.parseColor("#6ce3b7")
    private val dotColorMore = Color.parseColor("#acacac")

    private val space = 2f

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val oldColor = paint.color

        when (dotCount) {
            DotCount.One -> {
                paint.color = dotColor
                canvas.drawCircle((left + right) / 2f, bottom.toFloat(), radius, paint)
            }
            DotCount.Two -> {
                paint.color = dotColor
                val centerX = (left + right) / 2f
                canvas.drawCircle(centerX - radius - space, bottom.toFloat(), radius, paint)
                canvas.drawCircle(centerX + radius + space, bottom.toFloat(), radius, paint)
            }

            DotCount.More -> {
                paint.color = dotColorMore
                val centerX = (left + right) / 2f
                canvas.drawCircle(centerX, bottom.toFloat(), radius, paint)
                canvas.drawCircle(centerX - 2f * radius - space, bottom.toFloat(), radius, paint)
                canvas.drawCircle(centerX + 2f * radius + space, bottom.toFloat(), radius, paint)
            }
            DotCount.None -> {}
        }


        paint.color = oldColor
    }
}