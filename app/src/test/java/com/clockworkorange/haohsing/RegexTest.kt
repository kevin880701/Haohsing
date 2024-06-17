package com.clockworkorange.haohsing

import org.junit.Test

class RegexTest {


    @Test
    fun testRegex(){
        val testValue = "23/59 "
        val regex = Regex("(\\d{1,2})\\/(\\d{1,2}) ([0-1])")
        if (regex.matches(testValue)){
            val matchResult = regex.find(testValue)
            val groupValues = matchResult?.groupValues
            val timeHour = groupValues?.get(1)
            val timeMin = groupValues?.get(2)
            val onOff = groupValues?.get(3)

        }
    }
}