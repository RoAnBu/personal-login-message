package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.IPlayerLogins
import com.gmail.fantasticskythrow.configuration.TimeNames
import org.bukkit.entity.Player

class TimeReplacer(private val timeNames: TimeNames, private val playerLogins: IPlayerLogins): IPlaceholderReplacer {
    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        return getReplacedTime(message, player)
    }

    private fun getReplacedTime(inputText: String, player: Player): String {
        var message = inputText
        if (!message.contains("%time")) {
            return message
        }
        val timeSinceLastLoginMs: Long
        if (playerLogins.getLastLoginTimeMs(player) == 0L) {
            return message.replace("%time", timeNames.noLastLogin)
        } else {
            timeSinceLastLoginMs = playerLogins.getTimeSinceLastLoginMs(player)
        }
        // Less than 1 minute and is not 0
        if (timeSinceLastLoginMs < 60000L && timeSinceLastLoginMs != 0L) {
            val a = timeSinceLastLoginMs / 1000L
            message = if (a == 1L) {
                message.replace("%time", "$a ${timeNames.second}")
            } else {
                message.replace("%time", "$a ${timeNames.seconds}")
            }
        }
        // More than 1 minute and less than 1 hour
        if (timeSinceLastLoginMs in 60000L..3599999) {
            val a = timeSinceLastLoginMs / 60000L
            message = if (a == 1L) {
                message.replace("%time", "$a ${timeNames.minute}")
            } else {
                message.replace("%time", "$a ${timeNames.minutes}")
            }
        }
        // More than 1 hour and less than 1 day
        if (timeSinceLastLoginMs in 3600000L..86399999) {
            var a = timeSinceLastLoginMs / 60000L
            val rest = a % 60
            a /= 60
            message = if (a == 1L && rest == 0L) {
                message.replace("%time", "$a ${timeNames.hour}")
            } else if (rest == 0L) {
                message.replace("%time", "$a ${timeNames.hours}")
            } else if (a == 1L && rest == 1L) {
                message.replace("%time", "$a ${timeNames.hour} $rest ${timeNames.minute}")
            } else if (a == 1L) {
                message.replace("%time", "$a ${timeNames.hour} $rest ${timeNames.minutes}")
            } else if (rest == 1L) {
                message.replace("%time", "$a ${timeNames.hours} $rest ${timeNames.minute}")
            } else {
                message.replace("%time", "$a ${timeNames.hours} $rest ${timeNames.minutes}")
            }
        }
        // More than 1 day and less than 10 days
        if (timeSinceLastLoginMs in 86400000L..863999999) {
            var a = timeSinceLastLoginMs / 3600000L
            val rest = a % 24
            a /= 24
            message = if (a == 1L && rest == 0L) {
                message.replace("%time", "$a ${timeNames.day}")
            } else if (rest == 0L) {
                message.replace("%time", "$a ${timeNames.days}")
            } else if (a == 1L && rest == 1L) {
                message.replace("%time", "$a ${timeNames.day} $rest ${timeNames.hour}")
            } else if (a == 1L) {
                message.replace("%time", "$a ${timeNames.day} $rest ${timeNames.hours}")
            } else if (rest == 1L) {
                message.replace("%time", "$a ${timeNames.days} $rest ${timeNames.hour}")
            } else {
                message.replace("%time", "$a ${timeNames.days} $rest ${timeNames.hours}")
            }
        }
        // More than 10 days and less than 30 days
        if (timeSinceLastLoginMs in 864000000L..2591999999) {
            val a = timeSinceLastLoginMs / 86400000L
            message = if (a == 1L) {
                message.replace("%time", "$a ${timeNames.day}")
            } else {
                message.replace("%time", "$a ${timeNames.days}")
            }
        }
        // More than 1 month (30 days)
        if (timeSinceLastLoginMs >= 2592000000L) {
            val a = timeSinceLastLoginMs / 2592000000L
            message = if (a == 1L) {
                message.replace("%time", "$a ${timeNames.month}")
            } else {
                message.replace("%time", "$a ${timeNames.months}")
            }
        }
        return message
    }
}