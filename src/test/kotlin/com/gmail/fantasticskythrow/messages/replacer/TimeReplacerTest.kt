package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.IPlayerLogins
import com.gmail.fantasticskythrow.configuration.TimeNames
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
class TimeReplacerTest {
    @MockK
    lateinit var playerLogins: IPlayerLogins
    @MockK
    lateinit var player: Player

    private val timeNames = TimeNames.createEnglishTimeNames()

    lateinit var replacer: TimeReplacer

    @BeforeEach
    fun init() {
        replacer = TimeReplacer(timeNames, playerLogins)
    }

    @Test
    fun `no placeholders with null arguments, should not change the message`() {
        val message = "This is a test message"

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }

    @Test
    fun `%time no last login`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 0

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: ${timeNames.noLastLogin}", result)
    }

    @Test
    fun `%time, should return 1 second`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 1000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 second", result)
    }

    @Test
    fun `%time, should return 25 seconds`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 25000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 25 seconds", result)
    }

    @Test
    fun `%time, should return 1 minute`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 60000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 minute", result)
    }

    @Test
    fun `%time, should return 59 minutes`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 3540000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 59 minutes", result)
    }

    @Test
    fun `%time, should return 1 hour`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 3600000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 hour", result)
    }

    @Test
    fun `%time, should return 1 hour 1 minute`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 3660000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 hour 1 minute", result)
    }

    @Test
    fun `%time, should return 1 hour 45 minutes`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 6300000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 hour 45 minutes", result)
    }

    @Test
    fun `%time, should return 14 hours 1 minute`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 50460000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 14 hours 1 minute", result)
    }

    @Test
    fun `%time, should return 23 hours 59 minutes`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 86340000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 23 hours 59 minutes", result)
    }

    @Test
    fun `%time, should return 1 day`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 86400000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 day", result)
    }

    @Test
    fun `%time, should return 1 day, 1 hour`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 90000000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 day 1 hour", result)
    }

    @Test
    fun `%time, should return 1 day, 13 hours`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 133200000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 day 13 hours", result)
    }

    @Test
    fun `%time, should return 2 days, 1 hour`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 176400000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 2 days 1 hour", result)
    }

    @Test
    fun `%time, should return 10 days`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 864000000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 10 days", result)
    }

    @Test
    fun `%time, should return 25 days`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 2160000000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 25 days", result)
    }

    @Test
    fun `%time, should return 1 month`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 2592000000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 month", result)
    }

    @Test
    fun `%time, 50 days should return 1 month`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 4320000000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 1 month", result)
    }

    @Test
    fun `%time, 60 days should return 2 months`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 5184000000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 2 months", result)
    }

    @Test
    fun `%time, should return 60 months`() {
        val message = "Player joined again after: %time"

        every { playerLogins.getLastLoginTimeMs(player) } returns 1414602645000L
        every { playerLogins.getTimeSinceLastLoginMs(player) } returns 157788000000

        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined again after: 60 months", result)
    }

}