package com.clockworkorange.haohsing.ui.main.engineer.main.calendar

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.clockworkorange.haohsing.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectorDecorator(context: Context) : DayViewDecorator {

    var drawable: Drawable?

    init {
        drawable = ContextCompat.getDrawable(context, R.drawable.drawable_calendar_selector)
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setSelectionDrawable(drawable!!)
    }
}