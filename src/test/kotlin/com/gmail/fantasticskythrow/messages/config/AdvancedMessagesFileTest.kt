package com.gmail.fantasticskythrow.messages.config

import com.gmail.fantasticskythrow.configuration.IPluginFirstEnabled
import com.gmail.fantasticskythrow.messages.data.MessageData
import com.gmail.fantasticskythrow.messages.data.SectionSubTypes
import com.gmail.fantasticskythrow.messages.data.SectionTypes
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

@ExtendWith(MockKExtension::class)
class AdvancedMessagesFileTest {

    @MockK
    lateinit var pluginFirstEnabled: IPluginFirstEnabled

    private val file = File("src/test/resources/advancedMessages1.yml")

    private fun createAdvancedMessagesFile() = AdvancedMessagesFile(file, pluginFirstEnabled)

    @BeforeEach
    fun init() {
        every { pluginFirstEnabled.isPluginFirstEnabled } returns false
    }

    @Test
    fun `loading and parsing of file should work`() {
        createAdvancedMessagesFile()
    }

    @Test
    fun `joinMessage, should return default join message`() {

        val result = createAdvancedMessagesFile().getJoinMessage("Mike", null, null)
        val expected = MessageData("%chatplayername &ejoined the game",
                type = SectionTypes.DEFAULT,
                subType = SectionSubTypes.JOIN_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `quitMessage, should return default quit message`() {

        val result = createAdvancedMessagesFile().getQuitMessage("Mike", null)
        val expected = MessageData("%chatplayername &eleft the game",
                type = SectionTypes.DEFAULT,
                subType = SectionSubTypes.QUIT_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `newPlayerMessage, should return null`() {

        val result = createAdvancedMessagesFile().getNewPlayerMessage("Mike", null)

        Assertions.assertEquals(null, result)
    }

    @Test
    fun `newPlayerMessage, should return first message for player`() {

        val result = createAdvancedMessagesFile().getNewPlayerMessage("player2", null)
        val expected = MessageData("&aWelcome %playername to our server!",
                type = SectionTypes.PLAYER,
                subType = SectionSubTypes.FIRST_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `joinMessage for player, should return player join message`() {

        val result = createAdvancedMessagesFile().getJoinMessage("player2", null, null)
        val expected = MessageData("&aThis is the public join message",
                type = SectionTypes.PLAYER,
                subType = SectionSubTypes.JOIN_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `quitMessage for player, should return player quit message`() {

        val result = createAdvancedMessagesFile().getQuitMessage("player2", null)
        val expected = MessageData("&aThis is the public quit/leave message",
                type = SectionTypes.PLAYER,
                subType = SectionSubTypes.QUIT_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `welcomeMessages for player, should return 2 welcome messages`() {

        val result = createAdvancedMessagesFile().getWelcomeMessages("player2", null)
        val expected = listOf("&eThis message is for the player who is joining", "&2This would be another line just under WM1")

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `public messages for player, should return 2 public messages`() {

        val result = createAdvancedMessagesFile().getPublicMessages("player2", null)
        val expected = listOf("&eThis is a message for the other players on the server.", "&aYou can create more than one here, too")

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `joinMessage for player, group provided, should return join message from player section`() {

        val result = createAdvancedMessagesFile().getJoinMessage("player2", "examplegroup", null)
        val expected = MessageData("&aThis is the public join message",
                type = SectionTypes.PLAYER,
                subType = SectionSubTypes.JOIN_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `quitMessage for player, group provided should return quit message from player section`() {

        val result = createAdvancedMessagesFile().getQuitMessage("player2", "examplegroup")
        val expected = MessageData("&aThis is the public quit/leave message",
                type = SectionTypes.PLAYER,
                subType = SectionSubTypes.QUIT_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `welcomeMessages for player, group provided, should return 2 welcome messages from player section`() {

        val result = createAdvancedMessagesFile().getWelcomeMessages("player2", "examplegroup")
        val expected = listOf("&eThis message is for the player who is joining", "&2This would be another line just under WM1")

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `public messages for player, group provided, should return 2 public messages from player section`() {

        val result = createAdvancedMessagesFile().getPublicMessages("player2", "examplegroup")
        val expected = listOf("&eThis is a message for the other players on the server.", "&aYou can create more than one here, too")

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all join messages for player without group`() {
        val result = createAdvancedMessagesFile().getAllJoinMessages("player2", null).toSet()
        val expected = setOf(
                MessageData("%chatplayername &ejoined the game",
                        type = SectionTypes.DEFAULT,
                        subType = SectionSubTypes.JOIN_MESSAGE),
                MessageData("&aThis is the public join message",
                    type = SectionTypes.PLAYER,
                    subType = SectionSubTypes.JOIN_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all join messages for player with not fitting group`() {
        val result = createAdvancedMessagesFile().getAllJoinMessages("player2", "null").toSet()
        val expected = setOf(
                MessageData("%chatplayername &ejoined the game",
                        type = SectionTypes.DEFAULT,
                        subType = SectionSubTypes.JOIN_MESSAGE),
                MessageData("&aThis is the public join message",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.JOIN_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all join messages for player with fitting group`() {
        val result = createAdvancedMessagesFile().getAllJoinMessages("player2", "examplegroup").toSet()
        val expected = setOf(
                MessageData("%chatplayername &ejoined the game",
                        type = SectionTypes.DEFAULT,
                        subType = SectionSubTypes.JOIN_MESSAGE),
                MessageData("&aThis is the public join message",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.JOIN_MESSAGE),
                MessageData("&4Admin %playername joined the game",
                        type = SectionTypes.GROUP,
                        subType = SectionSubTypes.JOIN_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all quit messages for player without group`() {
        val result = createAdvancedMessagesFile().getAllQuitMessages("player2", null).toSet()
        val expected = setOf(
                MessageData("%chatplayername &eleft the game",
                        type = SectionTypes.DEFAULT,
                        subType = SectionSubTypes.QUIT_MESSAGE),
                MessageData("&aThis is the public quit/leave message",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.QUIT_MESSAGE),
                MessageData("A second quit message",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.QUIT_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all quit messages for player with not fitting group`() {
        val result = createAdvancedMessagesFile().getAllQuitMessages("player2", "null").toSet()
        val expected = setOf(
                MessageData("%chatplayername &eleft the game",
                        type = SectionTypes.DEFAULT,
                        subType = SectionSubTypes.QUIT_MESSAGE),
                MessageData("&aThis is the public quit/leave message",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.QUIT_MESSAGE),
                MessageData("A second quit message",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.QUIT_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all quit messages for player with fitting group`() {
        val result = createAdvancedMessagesFile().getAllQuitMessages("player2", "examplegroup").toSet()
        val expected = setOf(
                MessageData("%chatplayername &eleft the game",
                        type = SectionTypes.DEFAULT,
                        subType = SectionSubTypes.QUIT_MESSAGE),
                MessageData("&aThis is the public quit/leave message",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.QUIT_MESSAGE),
                MessageData("A second quit message",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.QUIT_MESSAGE),
                MessageData("&4Admin %playername left the game",
                        type = SectionTypes.GROUP,
                        subType = SectionSubTypes.QUIT_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all first messages for player without group`() {
        val result = createAdvancedMessagesFile().getAllNewPlayerMessages("player2", null).toSet()
        val expected = setOf(
                MessageData("&aWelcome %playername to our server!",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.FIRST_MESSAGE),
                MessageData("&cWelcome %playername to our server!",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.FIRST_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all first messages for player with not fitting group`() {
        val result = createAdvancedMessagesFile().getAllNewPlayerMessages("player2", "null").toSet()
        val expected = setOf(
                MessageData("&aWelcome %playername to our server!",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.FIRST_MESSAGE),
                MessageData("&cWelcome %playername to our server!",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.FIRST_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `all first messages for player with fitting group`() {
        val result = createAdvancedMessagesFile().getAllNewPlayerMessages("player2", "examplegroup").toSet()
        val expected = setOf(
                MessageData("&aWelcome %playername to our server!",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.FIRST_MESSAGE),
                MessageData("&cWelcome %playername to our server!",
                        type = SectionTypes.PLAYER,
                        subType = SectionSubTypes.FIRST_MESSAGE),
                MessageData("&4%playername in group examplegroup joined for the first time!",
                        type = SectionTypes.GROUP,
                        subType = SectionSubTypes.FIRST_MESSAGE))

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `back message, not fitting player, should return normal join message`() {
        val result = createAdvancedMessagesFile().getJoinMessage("non-fitting-player", null, 60000)
        val expected = MessageData("%chatplayername &ejoined the game",
                type = SectionTypes.DEFAULT,
                subType = SectionSubTypes.JOIN_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `back message, fitting player, not fitting time, should return normal join message`() {
        val result = createAdvancedMessagesFile().getJoinMessage("player2", null, 3600000)
        val expected = MessageData("&aThis is the public join message",
                type = SectionTypes.PLAYER,
                subType = SectionSubTypes.JOIN_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `back message, fitting player and fitting time, should return back message`() {
        val result = createAdvancedMessagesFile().getJoinMessage("player2", null, 3540000)
        val expected = MessageData("player2 is back after less than an hour!",
                type = SectionTypes.PLAYER,
                subType = SectionSubTypes.BACK_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `back message, fitting group and fitting time, should return back message 2`() {
        val result = createAdvancedMessagesFile().getJoinMessage("player123", "examplegroup", 3600000)
        val expected = MessageData("&4Admin &a%playername is back after some hours",
                type = SectionTypes.GROUP,
                subType = SectionSubTypes.BACK_MESSAGE)

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `back message, fitting group and fitting time, should return back message 1`() {
        val result = createAdvancedMessagesFile().getJoinMessage("player123", "examplegroup", 90000000)
        val expected = MessageData("&4Admin &a%playername is back after more than one day!",
                type = SectionTypes.GROUP,
                subType = SectionSubTypes.BACK_MESSAGE)

        Assertions.assertEquals(expected, result)
    }
}