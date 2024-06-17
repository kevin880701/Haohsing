package com.clockworkorange.haohsing.ui.main.engineer.main.calendar

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.clockworkorange.haohsing.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CurrentDayDecorator(private val context: Context, private val today: CalendarDay) :
    DayViewDecorator {

    private val drawable: Drawable?

    init {
        drawable = ContextCompat.getDrawable(context, R.drawable.drawable_calendar_today_background)
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == today
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(drawable!!)
        view?.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)))
    }
}