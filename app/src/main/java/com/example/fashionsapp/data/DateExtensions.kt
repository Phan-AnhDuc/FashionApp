package com.example.fashionsapp.data

import java.util.Calendar
import java.util.Date

fun getStartOfDay(currentTime: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.time = Date(currentTime)
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.timeInMillis
}