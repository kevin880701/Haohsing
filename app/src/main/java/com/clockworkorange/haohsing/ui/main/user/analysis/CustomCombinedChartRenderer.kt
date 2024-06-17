package com.clockworkorange.haohsing.ui.main.user.analysis

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.renderer.BubbleChartRenderer
import com.github.mikephil.charting.renderer.CandleStickChartRenderer
import com.github.mikephil.charting.renderer.CombinedChartRenderer
import com.github.mikephil.charting.renderer.ScatterChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomCombinedChartRenderer(
    chart: CombinedChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : CombinedChartRenderer(chart, animator, viewPortHandler) {

    override fun createRenderers() {
        mRenderers.clear()
        val chart = mChart.get() as CombinedChart? ?: return
        val orders = chart.drawOrder
        for (order in orders) {
            when (order) {
                DrawOrder.BAR -> if (chart.barData != null) mRenderers.add(
                    CustomBarChartRenderer(
                        chart,
                        mAnimator,
                        mViewPortHandler
                    )
                )
                DrawOrder.BUBBLE -> if (chart.bubbleData != null) mRenderers.add(
                    BubbleChartRenderer(
                        chart,
                        mAnimator,
                        mViewPortHandler
                    )
                )
                DrawOrder.LINE -> if (chart.lineData != null) mRenderers.add(
                    CustomLineChartRenderer(
                        chart,
                        mAnimator,
                        mViewPortHandler
                    )
                )
                DrawOrder.CANDLE -> if (chart.candleData != null) mRenderers.add(
                    CandleStickChartRenderer(chart, mAnimator, mViewPortHandler)
                )
                DrawOrder.SCATTER -> if (chart.scatterData != null) mRenderers.add(
                    ScatterChartRenderer(chart, mAnimator, mViewPortHandler)
                )
            }
        }
    }

}