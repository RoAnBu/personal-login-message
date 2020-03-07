package com.gmail.fantasticskythrow.messages.config

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.configuration.ExtendedYamlConfiguration
import com.gmail.fantasticskythrow.messages.IPLMFile
import com.gmail.fantasticskythrow.messages.config.exceptions.AdvancedMessagesFileException
import com.gmail.fantasticskythrow.messages.data.MessageData
import com.gmail.fantasticskythrow.messages.data.SectionSubTypes
import com.gmail.fantasticskythrow.messages.data.SectionTypes
import org.bukkit.configuration.Configuration
import java.io.File
import java.io.IOException
import kotlin.math.log

class AdvancedMessagesFile(private val advancedConfigFile: File,
                           private val plmFile: IPLMFile
                           ) : IAdvancedMessagesFile, IWorldRenameConfig {

    private lateinit var advancedMessagesYML: Configuration
    private val defaultPath = "Default"
    private val firstMsgPath = ".FM"
    private val joinMsgPath = ".JM"
    private val backMsgPath = ".BM"
    private val quitMsgPath = ".QM"
    private val welcomeMsgPath = ".WM"
    private val publicMsgPath = ".PM"

    init {
        loadAdvancedMessagesFile()
    }

    @Throws(AdvancedMessagesFileException::class)
    override fun reload() {
        loadAdvancedMessagesFile()
    }

    private fun loadAdvancedMessagesFile() {
        try {
            val yamlConfiguration = ExtendedYamlConfiguration.loadConfiguration(advancedConfigFile)
            advancedMessagesYML = yamlConfiguration
            if (!plmFile.firstEnabled || !advancedConfigFile.exists()) {
                advancedMessagesYML.set("Default.JM1", "%chatplayername &ejoined the game")
                advancedMessagesYML.set("Default.QM1", "%chatplayername &eleft the game")
                advancedMessagesYML.set("Groups.examplegroup.JM1", "&4Admin %playername joined the game")
                advancedMessagesYML.set("Groups.examplegroup.QM1", "&4Admin %playername left the game")
                advancedMessagesYML.set("Groups.examplegroup.BM1", "&4Admin &a%playername is back after more than one day!")
                advancedMessagesYML.set("Groups.examplegroup.BM1T", "1440")
                advancedMessagesYML.set("Groups.examplegroup.BM2", "&4Admin &a%playername is back after some hours")
                advancedMessagesYML.set("Groups.examplegroup.BM2T", "60")
                advancedMessagesYML.set("Groups.examplegroup.BM3", "&4Admin &a%playername is back after less than one hour!")
                advancedMessagesYML.set("Groups.examplegroup.BM3T", "-59")
                advancedMessagesYML.set("Groups.examplegroup.CH", "Messages, Information")
                advancedMessagesYML.set("players.exampleplayer.JM1", "&6The king Peter &2joined the server!")
                advancedMessagesYML.set("players.exampleplayer.QM1", "&6The king Peter &2left the server!")
                advancedMessagesYML.set("players.exampleplayer.JM2", "&2Our premium player Peter logged in!")
                advancedMessagesYML.set("players.exampleplayer.QM2", "&2Our premium player Peter is now offline!")
                advancedMessagesYML.set("players.exampleplayer2.JM1", "&aThis is the public join message")
                advancedMessagesYML.set("players.exampleplayer2.QM1", "&aThis is the public quit/leave message")
                advancedMessagesYML.set("players.exampleplayer2.WM1", "&eThis message is for the player who is joining")
                advancedMessagesYML.set("players.exampleplayer2.WM2", "&2This would be another line just under WM1")
                advancedMessagesYML.set("players.exampleplayer2.PM1", "&eThis is a message for the other players on the server.")
                advancedMessagesYML.set("players.exampleplayer2.PM2", "&aYou can create more than one here, too")
                advancedMessagesYML.set("World names.exampleworld", "main world")
                plmFile.firstEnabled = true
                yamlConfiguration.save(advancedConfigFile)
            }
        } catch (ex: IllegalStateException) {
            logger.error("Couldn't read AdvancedMessages.yml")
            logger.error("Please make sure that you used ' in front of any special character (%, &,...)")
            throw AdvancedMessagesFileException("Couldn't parse AdvancedMessages.yml", ex)
        } catch (io: IOException) {
            throw AdvancedMessagesFileException("Couldn't read AdvancedMessages.yml due to I/O problem", io)
        } catch (e: Exception) {
            throw AdvancedMessagesFileException("An unexpected error occurred while reading/parsing AdvancedMessages.yml", e)
        }
    }

    override fun getNewPlayerMessage(playerName: String, groupName: String?): MessageData? {
        val lowerPlayerName = playerName.toLowerCase()
        getFirstMessageForPathIfExists(getPlayerPath(lowerPlayerName) + firstMsgPath)?.let {
            return MessageData(it, type = SectionTypes.PLAYER, subType = SectionSubTypes.FIRST_MESSAGE)
        }
        if (groupName != null) {
            getFirstMessageForPathIfExists(getGroupPath(groupName) + firstMsgPath)?.let {
                return MessageData(it, type = SectionTypes.GROUP, subType = SectionSubTypes.FIRST_MESSAGE)
            }
        }
        getFirstMessageForPathIfExists(defaultPath + firstMsgPath)?.let {
            return MessageData(it, type = SectionTypes.DEFAULT, subType = SectionSubTypes.FIRST_MESSAGE)
        }
        return null
    }

    override fun getAllNewPlayerMessages(playerName: String, groupName: String?): List<MessageData> {
        val lowerPlayerName = playerName.toLowerCase()
        val messageDataList = ArrayList<MessageData>()
        getMessagesForPath(getPlayerPath(lowerPlayerName) + firstMsgPath).forEach {
            messageDataList.add(MessageData(it, type = SectionTypes.PLAYER, subType = SectionSubTypes.FIRST_MESSAGE))
        }
        if (groupName != null) {
            getMessagesForPath(getGroupPath(groupName) + firstMsgPath).forEach {
                messageDataList.add(MessageData(it, type = SectionTypes.GROUP, subType = SectionSubTypes.FIRST_MESSAGE))
            }
        }
        getMessagesForPath(defaultPath + firstMsgPath).forEach {
            messageDataList.add(MessageData(it, type = SectionTypes.DEFAULT, subType = SectionSubTypes.FIRST_MESSAGE))
        }
        return messageDataList
    }

    override fun getJoinMessage(playerName: String, groupName: String?, timeSinceLastLoginMs: Long?): MessageData? {
        val lowerPlayerName = playerName.toLowerCase()
        if (timeSinceLastLoginMs != null) {
            getBackMessageForPathIfExists(getPlayerPath(lowerPlayerName), timeSinceLastLoginMs)?.let {
                return MessageData(it, type = SectionTypes.PLAYER, subType = SectionSubTypes.BACK_MESSAGE)
            }
        }
        getFirstMessageForPathIfExists(getPlayerPath(lowerPlayerName) + joinMsgPath)?.let {
            return MessageData(it, type = SectionTypes.PLAYER, subType = SectionSubTypes.JOIN_MESSAGE)
        }
        if (groupName != null) {
            if (timeSinceLastLoginMs != null) {
                getBackMessageForPathIfExists(getGroupPath(groupName), timeSinceLastLoginMs)?.let {
                    return MessageData(it, type = SectionTypes.GROUP, subType = SectionSubTypes.BACK_MESSAGE)
                }
            }
            getFirstMessageForPathIfExists(getGroupPath(groupName) + joinMsgPath)?.let {
                return MessageData(it, type = SectionTypes.GROUP, subType = SectionSubTypes.JOIN_MESSAGE)
            }
        }
        if (timeSinceLastLoginMs != null) {
            getBackMessageForPathIfExists(defaultPath, timeSinceLastLoginMs)?.let {
                return MessageData(it, type = SectionTypes.DEFAULT, subType = SectionSubTypes.BACK_MESSAGE)
            }
        }
        getFirstMessageForPathIfExists(defaultPath + joinMsgPath)?.let {
            return MessageData(it, type = SectionTypes.DEFAULT, subType = SectionSubTypes.JOIN_MESSAGE)
        }
        return null
    }

    override fun getAllJoinMessages(playerName: String, groupName: String?, timeSinceLastLoginMs: Long?): List<MessageData> {
        val lowerPlayerName = playerName.toLowerCase()
        val messageDataList = ArrayList<MessageData>()
        if (timeSinceLastLoginMs != null) {
            getBackMessageForPathIfExists(getPlayerPath(lowerPlayerName), timeSinceLastLoginMs)?.let {
                messageDataList.add(MessageData(it, type = SectionTypes.PLAYER, subType = SectionSubTypes.BACK_MESSAGE))
            }
        }
        getMessagesForPath(getPlayerPath(lowerPlayerName) + joinMsgPath).forEach {
            messageDataList.add(MessageData(it, type = SectionTypes.PLAYER, subType = SectionSubTypes.JOIN_MESSAGE))
        }
        if (groupName != null) {
            if (timeSinceLastLoginMs != null) {
                getBackMessageForPathIfExists(getGroupPath(groupName), timeSinceLastLoginMs)?.let {
                    messageDataList.add(MessageData(it, type = SectionTypes.GROUP, subType = SectionSubTypes.BACK_MESSAGE))
                }
            }
            getMessagesForPath(getGroupPath(groupName) + joinMsgPath).forEach {
                messageDataList.add(MessageData(it, type = SectionTypes.GROUP, subType = SectionSubTypes.JOIN_MESSAGE))
            }
        }
        if (timeSinceLastLoginMs != null) {
            getBackMessageForPathIfExists(defaultPath, timeSinceLastLoginMs)?.let {
                messageDataList.add(MessageData(it, type = SectionTypes.DEFAULT, subType = SectionSubTypes.BACK_MESSAGE))
            }
        }
        getMessagesForPath(defaultPath + joinMsgPath).forEach {
            messageDataList.add(MessageData(it, type = SectionTypes.DEFAULT, subType = SectionSubTypes.JOIN_MESSAGE))
        }
        return messageDataList
    }

    override fun getQuitMessage(playerName: String, groupName: String?): MessageData? {
        val lowerPlayerName = playerName.toLowerCase()
        getFirstMessageForPathIfExists(getPlayerPath(lowerPlayerName) + quitMsgPath)?.let {
            return MessageData(it, type = SectionTypes.PLAYER, subType = SectionSubTypes.QUIT_MESSAGE)
        }
        if (groupName != null) {
            getFirstMessageForPathIfExists(getGroupPath(groupName) + quitMsgPath)?.let {
                return MessageData(it, type = SectionTypes.GROUP, subType = SectionSubTypes.QUIT_MESSAGE)
            }
        }
        getFirstMessageForPathIfExists(defaultPath + quitMsgPath)?.let {
            return MessageData(it, type = SectionTypes.DEFAULT, subType = SectionSubTypes.QUIT_MESSAGE)
        }
        return null
    }

    override fun getAllQuitMessages(playerName: String, groupName: String?): List<MessageData> {
        val lowerPlayerName = playerName.toLowerCase()
        val messageDataList = ArrayList<MessageData>()
        getMessagesForPath(getPlayerPath(lowerPlayerName) + quitMsgPath).forEach{
            messageDataList.add(MessageData(it, type = SectionTypes.PLAYER, subType = SectionSubTypes.QUIT_MESSAGE))
        }
        if (groupName != null) {
            getMessagesForPath(getGroupPath(groupName) + quitMsgPath).forEach {
                messageDataList.add(MessageData(it, type = SectionTypes.GROUP, subType = SectionSubTypes.QUIT_MESSAGE))
            }
        }
        getMessagesForPath(defaultPath + quitMsgPath).forEach {
            messageDataList.add(MessageData(it, type = SectionTypes.DEFAULT, subType = SectionSubTypes.QUIT_MESSAGE))
        }
        return messageDataList
    }

    override fun getWelcomeMessages(playerName: String, groupName: String?) = getAllMessagesOfTypeForPlayer(playerName, groupName, welcomeMsgPath)

    override fun getPublicMessages(playerName: String, groupName: String?) = getAllMessagesOfTypeForPlayer(playerName, groupName, publicMsgPath)

    private fun getAllMessagesOfTypeForPlayer(playerName: String, groupName: String?, messageTypeLabel: String): List<String> {
        val lowerPlayerName = playerName.toLowerCase()
        var messages: List<String> = getMessagesForPath(getPlayerPath(lowerPlayerName) + messageTypeLabel)
        if (messages.isNotEmpty())
            return messages
        if (groupName != null) {
            messages = getMessagesForPath(getGroupPath(groupName) + messageTypeLabel)
            if (messages.isNotEmpty())
                return messages
        }
        messages = getMessagesForPath(defaultPath + messageTypeLabel)
        return messages
    }

    private fun getBackMessageForPathIfExists(path: String, timeSinceLastJoinMs: Long): String? {
        var msgCounter = 1
        var message: String? = null
        while (advancedMessagesYML.contains(path + backMsgPath + msgCounter)) {
            val currentTimePath = path + backMsgPath + msgCounter + "T"
            if (advancedMessagesYML.contains(currentTimePath)) {
                val timeMs = advancedMessagesYML.getString(currentTimePath)?.toLongOrNull()?.times(60000)
                if (timeMs != null) {
                    if (timeMs in 1..timeSinceLastJoinMs) {
                        message = advancedMessagesYML.getString(path + backMsgPath + msgCounter)
                        break
                    } else if (timeMs < 0 && (timeMs * -1) >= timeSinceLastJoinMs) {
                        message = advancedMessagesYML.getString(path + backMsgPath + msgCounter)
                        break
                    }
                } else {
                    logger.info("Parsing number failed for back message at $path$backMsgPath$msgCounter")
                }
            } else {
                logger.info("Couldn't find the time path for back message at $path$backMsgPath$msgCounter")
            }
            msgCounter++
        }
        logger.trace("Back message $msgCounter for path $path: $message")
        return message
    }

    override fun getRenamedWorld(worldName: String): String? {
        return advancedMessagesYML.getString("World names.$worldName")
    }

    private fun getFirstMessageForPathIfExists(yamlPath: String): String? {
        return getMessagesForPath(yamlPath, 1).firstOrNull()
    }

    private fun getMessagesForPath(yamlPath: String, limit: Int = 0): List<String> {
        val messages = ArrayList<String>()
        var count = 1
        while (advancedMessagesYML.contains(yamlPath + count)) {
            messages.add(advancedMessagesYML.getString(yamlPath + count)!!)
            count++
            if (count == limit)
                break
        }
        return messages
    }

    private fun getPlayerPath(playerName: String): String {
        return String.format("players.%s", playerName)
    }

    private fun getGroupPath(groupName: String): String {
        return String.format("Groups.%s", groupName)
    }

    companion object {
        private val logger = PLM.logger()
    }

}