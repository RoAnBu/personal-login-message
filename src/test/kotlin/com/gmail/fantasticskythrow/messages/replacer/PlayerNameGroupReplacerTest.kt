package com.gmail.fantasticskythrow.messages.replacer

import com.earth2me.essentials.Essentials
import com.gmail.fantasticskythrow.configuration.IAppConfiguration
import com.gmail.fantasticskythrow.other.plugins.IPLMPluginConnector
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.UnsupportedOperationException
import java.util.*

@ExtendWith(MockKExtension::class)
class PlayerNameGroupReplacerTest {

    @MockK
    lateinit var chat: Chat
    @MockK
    lateinit var permission: Permission
    @MockK
    lateinit var pluginConnector: IPLMPluginConnector
    @MockK
    lateinit var appConfiguration: IAppConfiguration
    @MockK
    lateinit var player: Player
    @MockK
    lateinit var essentials: Essentials

    private fun createReplacerNullArgs() = PlayerNameGroupReplacer(null, null, pluginConnector, appConfiguration)
    private fun createReplacerFullArgs() = PlayerNameGroupReplacer(chat, permission, pluginConnector, appConfiguration)

    private val playerUUID = UUID.randomUUID()

    @BeforeEach
    fun init() {
        every { player.name } returns "Mike1337"
        every { player.uniqueId } returns playerUUID
    }

    @Test
    fun `no placeholders with null arguments, should not change the message`() {
        val message = "This is a test message"

        val replacer = createReplacerNullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }

    @Test
    fun `no placeholders with full arguments, should not change the message`() {
        val message = "This is a test message"

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }

    @Test
    fun `%playername placeholder, null arguments, should return player name`() {
        val message = "%playername joined the game"

        val replacer = createReplacerNullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Mike1337 joined the game", result)
    }

    @Test
    fun `%chatplayername placeholder, null arguments & essentials disabled, should return player name`() {
        val message = "%chatplayername joined the game"

        every { appConfiguration.useEssentialsNickName } returns false
        every { pluginConnector.essentials } returns null

        val replacer = createReplacerNullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Mike1337 joined the game", result)
    }

    @Test
    fun `%chatplayername placeholder, full arguments & essentials setup with empty prefix, should return essentials player name`() {
        val message = "%chatplayername joined the game"

        every { appConfiguration.useEssentialsNickName } returns true
        every { pluginConnector.essentials } returns essentials
        every { essentials.userMap.getUser(playerUUID).nickname } returns "Mike"
        every { essentials.settings.nicknamePrefix } returns ""
        every { chat.getPlayerPrefix(player) } returns ""
        every { chat.getPlayerSuffix(player) } returns ""

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Mike joined the game", result)
    }

    @Test
    fun `%chatplayername placeholder, full arguments & essentials setup with essentials prefix, should return essentials player name with prefix`() {
        val message = "%chatplayername joined the game"

        every { appConfiguration.useEssentialsNickName } returns true
        every { pluginConnector.essentials } returns essentials
        every { essentials.userMap.getUser(playerUUID).nickname } returns "Mike"
        every { essentials.settings.nicknamePrefix } returns "[Ess]"
        every { chat.getPlayerPrefix(player) } returns ""
        every { chat.getPlayerSuffix(player) } returns ""

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("[Ess]Mike joined the game", result)
    }

    @Test
    fun `%chatplayername placeholder, full arguments & essentials setup with chat prefix and suffix, should return essentials player name WITH prefix and suffix`() {
        val message = "%chatplayername joined the game"

        every { appConfiguration.useEssentialsNickName } returns true
        every { pluginConnector.essentials } returns essentials
        every { essentials.userMap.getUser(playerUUID).nickname } returns "Mike"
        every { essentials.settings.nicknamePrefix } returns ""
        every { chat.getPlayerPrefix(player) } returns "Prefix "
        every { chat.getPlayerSuffix(player) } returns " Suffix"

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Prefix Mike Suffix joined the game", result)
    }

    @Test
    fun `%nickname placeholder, full arguments & essentials setup with chat prefix and suffix, should return essentials player name WITHOUT prefix and suffix`() {
        val message = "%nickname joined the game"

        every { appConfiguration.useEssentialsNickName } returns true
        every { pluginConnector.essentials } returns essentials
        every { essentials.userMap.getUser(playerUUID).nickname } returns "Mike"
        every { essentials.settings.nicknamePrefix } returns ""
        every { chat.getPlayerPrefix(player) } returns "Prefix "
        every { chat.getPlayerSuffix(player) } returns " Suffix"

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Mike joined the game", result)
    }

    @Test
    fun `%nickname and %prefix placeholder, full arguments & essentials setup with chat prefix and suffix, should return essentials player name WITHOUT suffix`() {
        val message = "%nickname joined the game %prefix"

        every { appConfiguration.useEssentialsNickName } returns true
        every { pluginConnector.essentials } returns essentials
        every { essentials.userMap.getUser(playerUUID).nickname } returns "Mike"
        every { essentials.settings.nicknamePrefix } returns ""
        every { chat.getPlayerPrefix(player) } returns "Prefix"
        every { chat.getPlayerSuffix(player) } returns "Suffix"

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Mike joined the game Prefix", result)
    }

    @Test
    fun `%nickname and %suffix placeholder, full arguments & essentials setup with chat prefix and suffix, should return essentials player name WITHOUT prefix`() {
        val message = "%suffix %nickname joined the game"

        every { appConfiguration.useEssentialsNickName } returns true
        every { pluginConnector.essentials } returns essentials
        every { essentials.userMap.getUser(playerUUID).nickname } returns "Mike"
        every { essentials.settings.nicknamePrefix } returns ""
        every { chat.getPlayerPrefix(player) } returns "Prefix"
        every { chat.getPlayerSuffix(player) } returns "Suffix"

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Suffix Mike joined the game", result)
    }

    @Test
    fun `%group, full arguments, but no group provided, should return error message`() {
        val message = "%playername (%group) joined!"

        every { permission.getPrimaryGroup(player) } throws UnsupportedOperationException()

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Mike1337 (no group) joined!", result)
    }

    @Test
    fun `%group, full arguments, 1 group provided, should return only group`() {
        val message = "%playername (%group) joined!"

        every { permission.getPrimaryGroup(player) } returns "Warriors"

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Mike1337 (Warriors) joined!", result)
    }

    @Test
    fun `%group, full arguments, 2 groups provided, should return 1st group`() {
        val message = "%playername (%group) joined!"

        every { permission.getPrimaryGroup(player) } returns "Warriors"

        val replacer = createReplacerFullArgs()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals("Mike1337 (Warriors) joined!", result)
    }
}