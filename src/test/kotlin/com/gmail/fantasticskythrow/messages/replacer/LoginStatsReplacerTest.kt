package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.IPlayerLogins
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class LoginStatsReplacerTest {

    @MockK
    lateinit var playerLogins: IPlayerLogins
    @MockK
    lateinit var player: Player

    private fun createLoginStatsReplacer() = LoginStatsReplacer(playerLogins)

    @Test
    fun `no placeholders, should not change the message`() {
        val message = "This is a test message"

        val replacer = createLoginStatsReplacer()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }

    @Test
    fun `player login placeholder, should replace with correct count of login`() {
        val message = "Player joined (%logins logins)"
        every { playerLogins.getPlayerLogins(player) } returns 123
        val replacer = createLoginStatsReplacer()
        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (123 logins)", result)
    }

    @Test
    fun `total server logins placeholder, should replace with correct count of total logins`() {
        val message = "Player joined (%totallogins total logins)"
        every { playerLogins.totalLogins } returns 321
        val replacer = createLoginStatsReplacer()
        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (321 total logins)", result)
    }

    @Test
    fun `total unique logins placeholder, should replace with correct count of unique players`() {
        val message = "Player joined (%uniqueplayers unique players)"
        every { playerLogins.uniquePlayerLogins } returns 234
        val replacer = createLoginStatsReplacer()
        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (234 unique players)", result)
    }
}