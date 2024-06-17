package com.clockworkorange.haohsing.ui.widget

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.clockworkorange.domain.usecase.device.AreaValue
import com.clockworkorange.domain.usecase.device.DayMonthUsage
import com.clockworkorange.domain.usecase.device.HourValue
import com.clockworkorange.domain.usecase.task.TaskStatus
import com.clockworkorange.domain.usecase.task.TaskSummary
import com.clockworkorange.haohsing.R
import com.clockworkorange.haohsing.databinding.*
import com.clockworkorange.haohsing.ui.main.PhotoVideoAdapter
import com.clockworkorange.haohsing.ui.main.user.analysis.CustomCombinedChartRenderer
import com.clockworkorange.haohsing.utils.ScreenUtils
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import java.io.File
import java.time.Duration
import java.time.LocalDateTime

fun LayoutInfoItemBinding.setArrowOnly(title: String? = null) {
    title?.let { tvTitle.text = it }
    ivArrow.isVisible = true
    tvInfo.isVisible = false
}

fun LayoutInfoItemBinding.setTextOnly(title: String? = null, content: String? = null) {
    title?.let { tvTitle.text = it }
    content?.let { tvInfo.text = content }
    ivArrow.isVisible = false
    tvInfo.isVisible = true
    tvInfo.updateLayoutParams<ConstraintLayout.LayoutParams> {
        marginEnd = ScreenUtils.dp2px(tvInfo.context, 16)
    }
}

fun LayoutInfoItemBinding.setTextAndArrow(title: String? = null, content: String? = null) {
    title?.let { tvTitle.text = it }
    content?.let { tvInfo.text = content }
    ivArrow.isVisible = true
    tvInfo.isVisible = true
    tvInfo.updateLayoutParams<ConstraintLayout.LayoutParams> {
        marginEnd = ScreenUtils.dp2px(tvInfo.context, 52)
    }
}

fun LayoutInfoItemBinding.setBitmapFile(bitmapFile: File?) {
    bitmapFile ?: return
    ivArrow.isVisible = false

    tvInfo.updateLayoutParams<ConstraintLayout.LayoutParams> {
        marginEnd = ScreenUtils.dp2px(tvInfo.context, 16)
        width = ScreenUtils.dp2px(root.context, 56)
    }

    val bitmap = BitmapFactory.decodeStream(bitmapFile.inputStream())
    tvInfo.background = BitmapDrawable(root.context.resources, bitmap)
}

fun LayoutInfoItemBinding.setUrlImage(url: String?) {
    url?.ifEmpty { return }
    ivArrow.isVisible = false

    tvInfo.updateLayoutParams<ConstraintLayout.LayoutParams> {
        marginEnd = ScreenUtils.dp2px(tvInfo.context, 16)
        width = ScreenUtils.dp2px(root.context, 56)
    }

    Glide.with(this.root)
        .load(url)
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                tvInfo.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                tvInfo.background = null
            }
        })

}

fun LayoutInfoItemBinding.setListener(l: View.OnClickListener) {
    root.setOnClickListener(l)
}

fun LayoutCheckItemBinding.set(title: String) {
    tvTitle.text = title
    cbCheck.isClickable = false
}

fun LayoutCheckItemBinding.setListener(l: (Boolean) -> Unit) {
    root.setOnClickListener { l.invoke(cbCheck.isChecked) }
}

fun LayoutCheckItemBinding.setCheckStatus(isCheck: Boolean) {
    cbCheck.isChecked = isCheck
}

fun LayoutSelectItemBinding.set(title: String) {
    tvTitle.text = title
    rbSelect.isClickable = false
}

fun LayoutSelectItemBinding.setSelectStatus(selected: Boolean, isError: Boolean = false) {
    flRoot.setBackgroundResource(if (selected) R.drawable.shape_r10_bad6fb else R.drawable.shape_r10_white)
    if (isError) {
        flRoot.setBackgroundResource(R.drawable.shape_r10_white_red_border)
    }
    rbSelect.isChecked = selected
}


fun LayoutSelectItemBinding.setListener(l: (Boolean) -> Unit) {
    root.setOnClickListener { l.invoke(rbSelect.isChecked) }
}

fun LayoutSelectItemBinding.setCheck(isCheck: Boolean) {
    if (isCheck) {
        root.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#bad6fb"))
    } else {
        root.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
    }
}

fun LayoutSelectPhotoItemBinding.set(title: String) {
    tvTitle.text = title
    rvPhotos.layoutManager = GridLayoutManager(root.context, 3)
    rvPhotos.addItemDecoration(
        GridSpacingItemDecoration(
            3,
            ScreenUtils.dp2px(root.context, 8),
            false
        )
    )
    rvPhotos.adapter = PhotoVideoAdapter()
}

fun LayoutSelectPhotoItemBinding.setListener(l: View.OnClickListener) {
    flRoot.setOnClickListener(l)
}

fun LayoutSelectPhotoItemBinding.setRemoveListener(removeListener: () -> Unit) {
    (rvPhotos.adapter as? PhotoVideoAdapter)?.listener = object : PhotoVideoAdapter.Listener {
        override fun onRemoveItem(itemUri: Uri) {
            removeListener.invoke()
        }
    }
}

fun LayoutSelectPhotoItemBinding.setImageUri(uri: Uri?) {
    tvInfo.text = if (uri == null) "選擇照片" else "已選擇"
    rvPhotos.isVisible = uri != null
    (rvPhotos.adapter as? PhotoVideoAdapter)?.submitList(uri?.let { listOf(uri) } ?: listOf<Uri>())
}

fun ListitemTaskSummaryBinding.setUnFinishStyle(item: TaskSummary) {
    tvStatus.isVisible = false

    val hourDiff = Duration.between(LocalDateTime.now(), item.dateTime).toHours()
    if (hourDiff < 0) {//逾期
        tvStatus.isVisible = true
        tvStatus.text = "逾期"
        tvStatus.setBackgroundResource(0)
        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.colorRed))
        tvTime.setTextColor(ContextCompat.getColor(root.context, R.color.colorRed))
    } else if (hourDiff == 0L) {
        tvTime.setTextColor(ContextCompat.getColor(root.context, R.color.colorPrimary))
    } else {
        tvTime.setTextColor(ContextCompat.getColor(root.context, R.color.colorSecondary))
    }
}


fun ListitemTaskSummaryBinding.setFinishStyle(status: TaskStatus) {
    tvTime.setTextColor(ContextCompat.getColor(root.context, R.color.colorGray))
    tvStatus.isVisible = true
    if (status == TaskStatus.Finished) {
        tvStatus.text = root.context.getText(R.string.finished)
        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.colorPrimary))
        tvStatus.setBackgroundResource(R.drawable.shape_round_bad6fb)
    } else if (status == TaskStatus.Cancel) {
        tvStatus.text = root.context.getText(R.string.canceled)
        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.white))
        tvStatus.setBackgroundResource(R.drawable.shape_round_acacac)
    }
}


fun ListitemTaskSummaryBinding.setErrorStyle(item: TaskSummary) {
    tvTime.setTextColor(ContextCompat.getColor(root.context, R.color.colorGray))

    tvStatus.isVisible = true
    if (item.isStatusErrorNeedResend) {
        tvStatus.text = "需重派"
        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.white))
        tvStatus.setBackgroundResource(R.drawable.shape_round_f5c260)
    } else {
        tvStatus.text = "異常"
        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.white))
        tvStatus.setBackgroundResource(R.drawable.shape_round_ff7974)
    }
}

fun PanelSummaryWaterBinding.setData(usage: DayMonthUsage?) {

    if (usage == null) {
        return
    }

    root.isVisible = true

    tvTodayValue.text = usage.todayValue.toString()
    tvMonthValue.text = usage.monthValue.toString()

    when {
        usage.todayUpDown > 0 -> {
            tvTodayUpDown.text = "▲ %02d %%".format(usage.todayUpDown)
            tvTodayUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorRed))
        }
        usage.todayUpDown < 0 -> {
            tvTodayUpDown.text = "▼ %02d %%".format(usage.todayUpDown)
            tvTodayUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorLightGray))
        }
        usage.todayUpDown == 0 -> {
            tvTodayUpDown.text = "-- 0 %"
            tvTodayUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorLightGray))
        }
    }


    when {
        usage.monthUpDown > 0 -> {
            tvMonthUpDown.text = "▲ %02d %%".format(usage.todayUpDown)
            tvMonthUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorRed))
        }
        usage.monthUpDown < 0 -> {
            tvMonthUpDown.text = "▼ %02d %%".format(usage.todayUpDown)
            tvMonthUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorLightGray))
        }
        usage.monthUpDown == 0 -> {
            tvMonthUpDown.text = "-- 0 %"
            tvMonthUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorLightGray))
        }
    }

}

fun PanelSummaryPowerBinding.setData(usage: DayMonthUsage?) {

    if (usage == null) {
        root.isVisible = false
        return
    }

    root.isVisible = true

    tvTodayValue.text = usage.todayValue.toString()
    tvMonthValue.text = usage.monthValue.toString()

    when {
        usage.todayUpDown > 0 -> {
            tvTodayUpDown.text = "▲ %02d %%".format(usage.todayUpDown)
            tvTodayUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorRed))
        }
        usage.todayUpDown < 0 -> {
            tvTodayUpDown.text = "▼ %02d %%".format(usage.todayUpDown)
            tvTodayUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorLightGray))
        }
        usage.todayUpDown == 0 -> {
            tvTodayUpDown.text = " 0 %"
            tvTodayUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorLightGray))
        }
    }


    when {
        usage.monthUpDown > 0 -> {
            tvMonthUpDown.text = "▲ %02d %%".format(usage.todayUpDown)
            tvMonthUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorRed))
        }
        usage.monthUpDown < 0 -> {
            tvMonthUpDown.text = "▼ %02d %%".format(usage.todayUpDown)
            tvMonthUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorLightGray))
        }
        usage.monthUpDown == 0 -> {
            tvMonthUpDown.text = " 0 %"
            tvMonthUpDown.setTextColor(ContextCompat.getColor(root.context, R.color.colorLightGray))
        }
    }
}

fun PanelPieChartBinding.init(isWaterChart: Boolean) {
    chartUsage.apply {
        description = null
        isHighlightPerTapEnabled = false
        isDrawHoleEnabled = false
        setEntryLabelColor(ContextCompat.getColor(rootView.context, R.color.colorSecondary))
        isScrollContainer = false
        legend.isEnabled = false
    }

    tvTitle.text = if (isWaterChart) {
        "累積用水分布"
    } else {
        "累積用電分布"
    }
}

fun PanelPieChartBinding.setData(areaValues: List<AreaValue>?, isWaterChart: Boolean) {

    if (areaValues.isNullOrEmpty()) {
        chartUsage.data = null
        return
    }

    root.isVisible = true

    val maxValue = areaValues.maxByOrNull { it.value }
    maxValue?.let {
        tvMostUsage.text = "%s 佔 %d %%".format(it.name, it.value)
    }

    val pieEntry = areaValues.map { PieEntry(it.value.toFloat(), it.name) }

    val dataSet = PieDataSet(pieEntry, "").apply {
        setDrawValues(false)
    }

    if (isWaterChart) {
        dataSet.colors = listOf(
            Color.argb(255, 0, 142, 211),
            Color.argb(204, 0, 142, 211),
            Color.argb(153, 0, 142, 211),
            Color.argb(102, 0, 142, 211),
            Color.argb(51, 0, 142, 211),
            Color.argb(12, 0, 142, 211),
        )
    } else {
        dataSet.colors = listOf(
            Color.argb(255, 245, 194, 96),
            Color.argb(204, 245, 194, 96),
            Color.argb(153, 245, 194, 96),
            Color.argb(102, 245, 194, 96),
            Color.argb(51, 245, 194, 96),
            Color.argb(12, 245, 194, 96),
        )
    }

    chartUsage.apply {
        data = PieData(dataSet)
        invalidate()
    }
}

fun PanelHourUsageChartBinding.init() {

    chartHourUsage.apply {
        isHighlightPerDragEnabled = false
        isHighlightPerTapEnabled = false
        isDoubleTapToZoomEnabled = false
        isScaleYEnabled = false
        isScaleXEnabled = false
        isAutoScaleMinMaxEnabled = false

        renderer = CustomCombinedChartRenderer(this, this.animator, this.viewPortHandler)

        xAxis.apply {
            setDrawGridLines(false)
            // 因為 bar 的寬度是 0.9 因此他會以所在位置左右各佔 0.45，因此上下限至少要比 0 23 寬 0.45
            axisMinimum = -1f  // x 軸最小值
            axisMaximum = 23.5f   // x 軸最大值
            textColor = Color.parseColor("#666666")
            axisLineColor = Color.parseColor("#808080")
            axisLineWidth = 1f
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(false)
        }

        //water
        axisLeft.apply {
            axisMinimum = 0f    // y 軸最小值
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawBorders(false)
        }

        //power
        axisRight.apply {
            axisMinimum = 0f
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawBorders(false)
            spaceTop = 15f
        }

        description = Description().apply { text = "" }
        legend.isEnabled = false

    }
}

fun PanelHourUsageChartBinding.setData(
    waterHourUsage: List<HourValue>?,
    powerHourUsage: List<HourValue>?
) {
    if (powerHourUsage == null && waterHourUsage == null) {
        chartHourUsage.data = null
    }

    llLegendPower.isVisible = powerHourUsage != null
    waterHourUsage ?: return

    // region water
    val waterEntry = waterHourUsage.map{
        BarEntry(
            it.hour.toFloat(),
            it.value.toFloat()
        )
    }

    val waterDataSet = BarDataSet(waterEntry, "water").apply {
        color = Color.parseColor("#BAD6FB")
        highLightColor = Color.parseColor("#008ED3")
        highLightAlpha = 255
        setDrawValues(false)
        axisDependency = YAxis.AxisDependency.LEFT
        isHighlightEnabled = true
    }

    val waterBarData = BarData(waterDataSet).apply {
        barWidth = 0.9f
    }

    val combinedData = CombinedData().apply {
        setData(waterBarData)
    }
    // endregion

    // region power
    if (powerHourUsage != null) {
        val powerEntry = powerHourUsage.map {
            Entry(
                it.hour.toFloat(),
                it.value.toFloat()
            )
        }
        val powerDataSet = LineDataSet(powerEntry, "power").apply {
            color = Color.parseColor("#F5C260")
            valueTextColor = Color.parseColor("#F5C260")
            valueTextSize = 14f
            highLightColor = Color.parseColor("#F5C260")
            setDrawValues(false)
            setDrawCircles(false)
            setDrawCircleHole(false)
            setDrawHighlightIndicators(false)
            isHighlightEnabled = true
            axisDependency = YAxis.AxisDependency.RIGHT
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineWidth = 2f
        }

        combinedData.setData(LineData(powerDataSet))

        val avgPower = powerHourUsage.map{ it.value }.average()
        val avgLine = LimitLine(avgPower.toFloat(), "平均%.0f度".format(avgPower)).apply {
            textSize = 10f
            textColor = Color.parseColor("#F5C260")
            lineColor = Color.parseColor("#F5C260")
            lineWidth = 1f
            enableDashedLine(5f, 4f, 0f)
        }

        chartHourUsage.axisRight.apply {
            setDrawLimitLinesBehindData(false)
            removeAllLimitLines()
            addLimitLine(avgLine)
        }
    }
    // endregion

    chartHourUsage.apply {
        data = combinedData

        val highlights = mutableListOf<Highlight>()
        val highLightWaterEntry = waterEntry.maxByOrNull { it.y }
        if (highLightWaterEntry != null) {
            axisLeft.axisMaximum = highLightWaterEntry.y + 5
            val dataIndex = combinedData.allData.indexOfFirst { it is BarData }
            val highlight = Highlight(highLightWaterEntry.x, highLightWaterEntry.y, 0)
            highlight.dataIndex = dataIndex
            highlights.add(highlight)
        }

        if (powerHourUsage != null) {
            val highLightPowerEntry = powerHourUsage
                .map { Entry(it.hour.toFloat(), it.value.toFloat()) }
                .maxByOrNull { it.y }
            if (highLightPowerEntry != null) {
                val dataIndex = combinedData.allData.indexOfFirst { it is LineData }
                val highlight = Highlight(highLightPowerEntry.x, highLightPowerEntry.y, 0)
                highlight.dataIndex = dataIndex
                highlights.add(highlight)
            }
        }

        highlightValues(highlights.toTypedArray())
        xAxis.setLabelCount(12, false)
        invalidate()
    }
}