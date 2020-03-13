package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.other.IVanishManager
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.bukkit.Server
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ServerStatsReplacerTest {

    @MockK
    lateinit var server: Server
    @MockK
    lateinit var vanishManager: IVanishManager
    @MockK
    lateinit var player: Player
    @MockK
    lateinit var player2: Player
    @MockK
    lateinit var player3: Player

    @BeforeEach
    fun initPlayerNames() {
        every { player.name } returns "Mike"
        every { player2.name } returns "Arnold"
        every { player3.name } returns "Claire"
    }

    private fun createServerStatsReplacer() = ServerStatsReplacer(server, vanishManager)

    @Test
    fun `no placeholders with null arguments, should not change the message`() {
        val message = "This is a test message"

        val replacer = createServerStatsReplacer()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }

    @Test
    fun `%slots, should return max allowed players on server`() {
        val message = "Player joined (max. players: %slots)"

        every { server.maxPlayers } returns 20

        val replacer =  createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (max. players: 20)", result)
    }

    private fun noPlayerVanished() {
        every { vanishManager.isVanished(any()) } returns false
    }

    @Test
    fun `%onlineplayers, no players vanished, 1 online player`() {
        noPlayerVanished()

        every { server.onlinePlayers } returns mutableListOf(player)

        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Currently are 1 players online!", result)
    }

    @Test
    fun `%onlineplayers, no players vanished, 3 online players`() {
        noPlayerVanished()

        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)

        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Currently are 3 players online!", result)
    }

    @Test
    fun `%onlineplayers, 1 player vanished, 2 online players`() {
        every { vanishManager.isVanished("Mike") } returns false
        every { vanishManager.isVanished("Arnold") } returns true
        every { vanishManager.isVanished("Claire") } returns false

        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)

        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Currently are 2 players online!", result)
    }

    @Test
    fun `%onlineplayers, 3 player vanished, 0 online players`() {
        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)

        every { vanishManager.isVanished("Mike") } returns true
        every { vanishManager.isVanished("Arnold") } returns true
        every { vanishManager.isVanished("Claire") } returns true


        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player)

        Assertions.assertEquals("Currently are 0 players online!", result)
    }

    @Test
    fun `%onlineplayers, 0 player vanished, quitting, 2 online players`() {
        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)

        every { vanishManager.isVanished("Mike") } returns false
        every { vanishManager.isVanished("Arnold") } returns false
        every { vanishManager.isVanished("Claire") } returns false


        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player, true)

        Assertions.assertEquals("Currently are 2 players online!", result)
    }

    @Test
    fun `%onlineplayers, 1 player vanished, quitting, 1 online players`() {
        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)

        every { vanishManager.isVanished("Mike") } returns false
        every { vanishManager.isVanished("Arnold") } returns true
        every { vanishManager.isVanished("Claire") } returns false


        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player, true)

        Assertions.assertEquals("Currently are 1 players online!", result)
    }

    @Test
    fun `%onlineplayers, all players vanished, quitting, 0 online players`() {
        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)

        every { vanishManager.isVanished("Mike") } returns true
        every { vanishManager.isVanished("Arnold") } returns true
        every { vanishManager.isVanished("Claire") } returns true


        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player, true)

        Assertions.assertEquals("Currently are 0 players online!", result)
    }

    @Test
    fun `%onlineplayers, all players vanished, not quitting, 0 online players`() {
        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)

        every { vanishManager.isVanished("Mike") } returns true
        every { vanishManager.isVanished("Arnold") } returns true
        every { vanishManager.isVanished("Claire") } returns true


        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player, false)

        Assertions.assertEquals("Currently are 0 players online!", result)
    }

    @Test
    fun `%onlineplayers, 2 players vanished, not quitting, 1 online players`() {
        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)

        every { vanishManager.isVanished("Mike") } returns false
        every { vanishManager.isVanished("Arnold") } returns true
        every { vanishManager.isVanished("Claire") } returns true


        val message = "Currently are %onlineplayers players online!"
        val replacer = createServerStatsReplacer()
        val result = replacer.replacePlaceholders(message, player, false)

        Assertions.assertEquals("Currently are 1 players online!", result)
    }

}