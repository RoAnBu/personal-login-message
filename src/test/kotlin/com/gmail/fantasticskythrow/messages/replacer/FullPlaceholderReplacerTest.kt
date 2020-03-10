package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.IAppConfiguration
import com.gmail.fantasticskythrow.configuration.PLMFile
import com.gmail.fantasticskythrow.configuration.TimeNames
import com.gmail.fantasticskythrow.messages.config.IWorldRenameConfig
import com.gmail.fantasticskythrow.other.IVanishManager
import com.gmail.fantasticskythrow.other.plugins.IIPAddressLookup
import com.gmail.fantasticskythrow.other.plugins.IPLMPluginConnector
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
class FullPlaceholderReplacerTest {

    @MockK
    lateinit var chat: Chat
    @MockK
    lateinit var permission: Permission
    @MockK
    lateinit var plmFile: PLMFile
    @MockK
    lateinit var vanishManager: IVanishManager
    @MockK
    lateinit var worldRenameConfig: IWorldRenameConfig
    @MockK
    lateinit var server: Server
    @MockK
    lateinit var appConfiguration: IAppConfiguration
    @MockK
    lateinit var pluginConnector: IPLMPluginConnector
    @MockK
    lateinit var player: Player
    @MockK
    lateinit var ipLookup: IIPAddressLookup

    val timeNames = TimeNames.createEnglishTimeNames()

    @BeforeEach
    fun setup() {
        every { player.name } returns "Mike"
        every { pluginConnector.ipLookup } returns ipLookup
    }

    @Test
    fun `Message with no Placeholder, message should be unchanged`() {
        val fullPlaceholderReplacer = FullPlaceholderReplacer(chat, permission, plmFile, vanishManager, timeNames,
                server, appConfiguration, pluginConnector, null)

        val message = "This is a test message"
        val result = fullPlaceholderReplacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }

    @Test
    fun `Message with no Placeholder, with WorldRenameConfig, message should be unchanged`() {
        val fullPlaceholderReplacer = FullPlaceholderReplacer(chat, permission, plmFile, vanishManager, timeNames,
                server, appConfiguration, pluginConnector, worldRenameConfig)

        val message = "This is a test message"
        val result = fullPlaceholderReplacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }
}