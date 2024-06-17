package com.clockworkorange.haohsing.ui.main.engineer.main.calendar

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class OneEventDecorator(private val days: List<CalendarDay>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day in days
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(CustomDotSpan(CustomDotSpan.DotCount.One))
    }
}