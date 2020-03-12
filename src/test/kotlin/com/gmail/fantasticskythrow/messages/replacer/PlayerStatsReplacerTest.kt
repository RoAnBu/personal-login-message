package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.messages.config.IWorldRenameConfig
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PlayerStatsReplacerTest {

    @MockK
    lateinit var worldRenameConfig: IWorldRenameConfig
    @MockK
    lateinit var player: Player

    @BeforeEach
    fun init() {
        every { player.name } returns "Mike"
    }

    private fun createPlayerStatsReplacerNullArg() = PlayerStatsReplacer()
    private fun createPlayerStatsReplacerNormalArgs() = PlayerStatsReplacer(worldRenameConfig)

    @Test
    fun `no placeholders with null arguments, should not change the message`() {
        val message = "This is a test message"

        val replacer = createPlayerStatsReplacerNullArg()

        val result = replacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)
    }

    @Test
    fun `%levels, should return levels number`() {
        val message = "Player joined (level: %levels)"

        every { player.level } returns 12

        val result = createPlayerStatsReplacerNullArg().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (level: 12)", result)
    }

    @Test
    fun `%health, should return health number`() {
        val message = "Player joined (health: %health)"

        every { player.health } returns 12.0

        val result = createPlayerStatsReplacerNullArg().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (health: 12.0)", result)
    }

    @Test
    fun `%comparedHealth, should return health number and max health`() {
        val message = "Player joined (health: %comparedHealth)"

        every { player.health } returns 12.0
        every { player.getAttribute(Attribute.GENERIC_MAX_HEALTH).toString() } returns "20.0"

        val result = createPlayerStatsReplacerNullArg().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (health: 12.0/20.0)", result)
    }

    @Test
    fun `%gamemode, should return english game mode string`() {
        val message = "Player joined (game mode: %gamemode)"

        every { player.gameMode } returns GameMode.CREATIVE

        val result = createPlayerStatsReplacerNullArg().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (game mode: Creative)", result)
    }

    @Test
    fun `%food, should return food int value`() {
        val message = "Player joined (food: %food)"

        every { player.foodLevel } returns 12

        val result = createPlayerStatsReplacerNullArg().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (food: 12)", result)
    }

    @Test
    fun `%world, without rename config, should return default world name`() {
        val message = "Player joined (World: %world)"

        every { player.world.name } returns "skylines_nether"

        val result = createPlayerStatsReplacerNullArg().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (World: skylines_nether)", result)
    }

    @Test
    fun `%World, without rename config, should return default world name formatted`() {
        val message = "Player joined (World: %World)"

        every { player.world.name } returns "skylines_nether"

        val result = createPlayerStatsReplacerNullArg().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (World: Skylines nether)", result)
    }

    @Test
    fun `%world, with rename config without alternate name, should return default world name unformatted`() {
        val message = "Player joined (World: %world)"

        every { player.world.name } returns "skylines_nether"
        every { worldRenameConfig.getRenamedWorld("skylines_nether") } returns null

        val result = createPlayerStatsReplacerNormalArgs().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (World: skylines_nether)", result)
    }

    @Test
    fun `%World, with rename config without alternate name, should return default world name formatted`() {
        val message = "Player joined (World: %World)"

        every { player.world.name } returns "skylines_nether"
        every { worldRenameConfig.getRenamedWorld("skylines_nether") } returns null

        val result = createPlayerStatsReplacerNormalArgs().replacePlaceholders(message, player)

        Assertions.assertEquals("Player joined (World: Skylines nether)", result)
    }

    @Test
    fun `%World and %world, with rename config with alternate name, should return alternate name`() {
        val message = "Player joined (World: %World)"
        val message2 = "Player joined (World: %world)"

        every { player.world.name } returns "skylines_nether"
        every { worldRenameConfig.getRenamedWorld("skylines_nether") } returns "Skylines [Nether]"

        val result = createPlayerStatsReplacerNormalArgs().replacePlaceholders(message, player)
        Assertions.assertEquals("Player joined (World: Skylines [Nether])", result)
        val result2 = createPlayerStatsReplacerNormalArgs().replacePlaceholders(message2, player)
        Assertions.assertEquals("Player joined (World: Skylines [Nether])", result2)
    }
}