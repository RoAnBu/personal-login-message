package com.gmail.fantasticskythrow.configuration

data class TimeNames(val second: String, val seconds: String, val minute: String, val minutes: String, val hour: String,
                     val hours: String, val day: String, val days: String, val month: String, val months: String,
                     val noLastLogin: String) {
    companion object {
        fun createEnglishTimeNames() = TimeNames("second", "seconds", "minute", "minutes",
                "hour", "hours", "day", "days", "month", "months",
                "No last login")
    }
}
