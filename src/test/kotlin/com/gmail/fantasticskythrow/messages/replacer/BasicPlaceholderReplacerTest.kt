package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.IAppConfiguration
import com.gmail.fantasticskythrow.configuration.TimeNames
import com.gmail.fantasticskythrow.messages.IPLMFile
import com.gmail.fantasticskythrow.messages.config.IWorldRenameConfig
import com.gmail.fantasticskythrow.other.IVanishManager
import com.gmail.fantasticskythrow.other.plugins.IPLMPluginConnector
import io.mockk.MockKAnnotations
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
class BasicPlaceholderReplacerTest {

    @MockK
    lateinit var chat: Chat
    @MockK
    lateinit var permission: Permission
    @MockK
    lateinit var plmFile: IPLMFile
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

    val timeNames = TimeNames.createEnglishTimeNames()

    lateinit var basicPlaceholderReplacer: BasicPlaceholderReplacer

    @BeforeEach
    fun setup() {

        basicPlaceholderReplacer = BasicPlaceholderReplacer(chat, permission, plmFile, vanishManager, timeNames,
                worldRenameConfig, server, appConfiguration, pluginConnector)

        every { player.name } returns "Mike"
    }

    @Test
    fun `Message with no Placeholder, message should be unchanged`() {
        val message = "This is a test message"
        val result = basicPlaceholderReplacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }
}