package com.example.lab1.model

import java.text.SimpleDateFormat
import java.util.*

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val description: String?,
    val location: String?
) {
    fun getFormattedStartDate(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date(startTime))
    }

    fun getFormattedEndDate(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date(endTime))
    }

    fun getDuration(): String {
        val durationMillis = endTime - startTime
        val hours = durationMillis / (1000 * 60 * 60)
        val minutes = (durationMillis / (1000 * 60)) % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }
}