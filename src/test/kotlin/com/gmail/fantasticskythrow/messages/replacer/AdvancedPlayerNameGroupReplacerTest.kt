package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.other.IVanishManager
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.Server
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AdvancedPlayerNameGroupReplacerTest {

    @MockK
    lateinit var chat: Chat
    @MockK
    lateinit var permission: Permission
    @MockK
    lateinit var server: Server
    @MockK
    lateinit var vanishManager: IVanishManager
    @MockK
    lateinit var playerNameGroupReplacer: PlayerNameGroupReplacer
    @MockK
    lateinit var player: Player
    @MockK
    lateinit var player2: Player
    @MockK
    lateinit var player3: Player

    private fun createReplacerNullArgs() = AdvancedPlayerNameGroupReplacer(null, null, server, vanishManager, playerNameGroupReplacer)
    private fun createReplacerChatOnly() = AdvancedPlayerNameGroupReplacer(chat, null, server, vanishManager, playerNameGroupReplacer)
    private fun createReplacerPermissionOnly() = AdvancedPlayerNameGroupReplacer(null, permission, server, vanishManager, playerNameGroupReplacer)
    private fun createReplacerAllArgs() = AdvancedPlayerNameGroupReplacer(chat, permission, server, vanishManager, playerNameGroupReplacer)

    @BeforeEach
    fun initPlayerNames() {
        every { player.name } returns "Mike"
        every { player2.name } returns "Arnold"
        every { player3.name } returns "Claire"
    }

    @Test
    fun `no placeholders with null arguments, should not change the message`() {
        val message = "This is a test message"

        val result = createReplacerAllArgs().replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }

    @Test
    fun `%playerlist, no players, list should be empty`() {
        val message = "Currently online players: %playerlist"

        every { server.onlinePlayers } returns mutableListOf()

        val result = createReplacerNullArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: ", result)
    }

    @Test
    fun `%playerlist, 1 vanished player, list should be empty`() {
        val message = "Currently online players: %playerlist"

        every { server.onlinePlayers } returns mutableListOf(player)
        every { vanishManager.isVanished(player) } returns true

        val result = createReplacerNullArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: ", result)
    }

    @Test
    fun `%playerlist, 2 vanished players, list should be empty`() {
        val message = "Currently online players: %playerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player2)
        every { vanishManager.isVanished(player) } returns true
        every { vanishManager.isVanished(player2) } returns true

        val result = createReplacerNullArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: ", result)
    }

    @Test
    fun `%playerlist, 1 vanished player, 1 online player, 1 player should be in list`() {
        val message = "Currently online players: %playerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns true

        val result = createReplacerNullArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike", result)
    }

    @Test
    fun `%playerlist, 1 vanished player, 2 online players, 2 players should be in list`() {
        val message = "Currently online players: %playerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns true
        every { vanishManager.isVanished(player3) } returns false

        val result = createReplacerNullArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Claire, Mike", result)
    }

    @Test
    fun `%playerlist, 3 online players, 3 players should be in list`() {
        val message = "Currently online players: %playerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player2, player3)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns false
        every { vanishManager.isVanished(player3) } returns false

        val result = createReplacerNullArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Arnold, Claire, Mike", result)
    }

    @Test
    fun `%chatplayerlist, 1 online player, but no chat plugin, 1 player should be in list without replaced name`() {
        val message = "Currently online players: %chatplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player)
        every { vanishManager.isVanished(player) } returns false
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player) } returns "Mike1337"

        val result = createReplacerNullArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike", result)
    }

    @Test
    fun `%chatplayerlist, 1 online player, 1 vanished, 0 players should be in list`() {
        val message = "Currently online players: %chatplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player)
        every { vanishManager.isVanished(player) } returns true
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player) } returns "Mike1337"

        val result = createReplacerChatOnly().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: ", result)
    }

    @Test
    fun `%chatplayerlist, 1 online player, 1 player should be in list with replaced name`() {
        val message = "Currently online players: %chatplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player)
        every { vanishManager.isVanished(player) } returns false
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player) } returns "Mike1337"

        val result = createReplacerChatOnly().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike1337", result)
    }

    @Test
    fun `%chatplayerlist, 2 online players, 2 players should be in list with replaced name`() {
        val message = "Currently online players: %chatplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns false
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player) } returns "Mike1337"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player2) } returns "TheHunter"

        val result = createReplacerChatOnly().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike1337, TheHunter", result)
    }

    @Test
    fun `%chatplayerlist, 3 online players, 1 vanished, 2 players should be in list with replaced name`() {
        val message = "Currently online players: %chatplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player3, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns false
        every { vanishManager.isVanished(player3) } returns true
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player) } returns "Mike1337"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player2) } returns "TheHunter"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player3) } returns "Invisible"

        val result = createReplacerChatOnly().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike1337, TheHunter", result)
    }

    @Test
    fun `%groupplayerlist, 3 online players, 1 in group, 1 player should be in list`() {
        val message = "Currently online players: %groupplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player3, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns false
        every { vanishManager.isVanished(player3) } returns false
        every { permission.getPrimaryGroup(player) } returns "Group1"
        every { permission.getPrimaryGroup(player2) } returns "Group2"
        every { permission.getPrimaryGroup(player3) } returns "Group2"

        val result = createReplacerPermissionOnly().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike", result)
    }

    @Test
    fun `%groupplayerlist, 3 online players, 2 in group, 1 vanished, 1 player should be in list`() {
        val message = "Currently online players: %groupplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player3, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns true
        every { vanishManager.isVanished(player3) } returns true
        every { permission.getPrimaryGroup(player) } returns "Group1"
        every { permission.getPrimaryGroup(player2) } returns "Group1"
        every { permission.getPrimaryGroup(player3) } returns "Group2"

        val result = createReplacerPermissionOnly().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike", result)
    }

    @Test
    fun `%groupplayerlist, 3 online players, 2 in group, 2 players should be in list`() {
        val message = "Currently online players: %groupplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player3, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns false
        every { vanishManager.isVanished(player3) } returns false
        every { permission.getPrimaryGroup(player) } returns "Group1"
        every { permission.getPrimaryGroup(player2) } returns "Group1"
        every { permission.getPrimaryGroup(player3) } returns "Group2"

        val result = createReplacerPermissionOnly().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Arnold, Mike", result)
    }

    @Test
    fun `%groupchatplayerlist, 3 online players, 1 in group, 1 player should be in list`() {
        val message = "Currently online players: %groupchatplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player3, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns false
        every { vanishManager.isVanished(player3) } returns false
        every { permission.getPrimaryGroup(player) } returns "Group1"
        every { permission.getPrimaryGroup(player2) } returns "Group2"
        every { permission.getPrimaryGroup(player3) } returns "Group2"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player) } returns "Mike1337"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player2) } returns "TheHunter"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player3) } returns "AA_123"

        val result = createReplacerAllArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike1337", result)
    }

    @Test
    fun `%groupchatplayerlist, 3 online players, 2 in group, 1 vanished, 1 player should be in list`() {
        val message = "Currently online players: %groupchatplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player3, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns true
        every { vanishManager.isVanished(player3) } returns true
        every { permission.getPrimaryGroup(player) } returns "Group1"
        every { permission.getPrimaryGroup(player2) } returns "Group1"
        every { permission.getPrimaryGroup(player3) } returns "Group2"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player) } returns "Mike1337"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player2) } returns "TheHunter"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player3) } returns "AA_123"

        val result = createReplacerAllArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike1337", result)
    }

    @Test
    fun `%groupchatplayerlist, 3 online players, 2 in group, 2 players should be in list`() {
        val message = "Currently online players: %groupchatplayerlist"

        every { server.onlinePlayers } returns mutableListOf(player, player3, player2)
        every { vanishManager.isVanished(player) } returns false
        every { vanishManager.isVanished(player2) } returns false
        every { vanishManager.isVanished(player3) } returns false
        every { permission.getPrimaryGroup(player) } returns "Group1"
        every { permission.getPrimaryGroup(player2) } returns "Group1"
        every { permission.getPrimaryGroup(player3) } returns "Group2"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player) } returns "Mike1337"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player2) } returns "TheHunter"
        every { playerNameGroupReplacer.getReplacedChatplayername("%chatplayername", player3) } returns "AA_123"

        val result = createReplacerAllArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Currently online players: Mike1337, TheHunter", result)
    }
}
