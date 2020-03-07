package com.gmail.fantasticskythrow.messages.config

import com.gmail.fantasticskythrow.PLM
import java.io.*

class StandardMessagesFile(private val messagesFile: File) : IStandardMessagesFile {
    override lateinit var joinMessage: String
        private set
    override lateinit var quitMessage: String
        private set

    init {
        loadMessages()
    }

    override fun reload() {
        loadMessages()
    }

    private fun loadMessages() {
        createMessagesConfigFileIfNotExits()
        try {
            val fr = FileReader(messagesFile)
            val br = BufferedReader(fr)
            br.readLine()
            val joinMessage = br.readLine()
            br.readLine()
            br.readLine()
            val quitMessage = br.readLine()
            br.close()
            this.joinMessage = joinMessage
            this.quitMessage = quitMessage
        } catch (e: Exception) {
            logger.error("An error has occurred while reading messages.txt")
            logger.error("Please check the messages.txt file. Standard join and quit messages will be used")
            logger.error(e)
            joinMessage = "&e%playername joined the game"
            quitMessage = "&e%playername left the game"
        }
    }

    private fun createMessagesConfigFileIfNotExits() {
        try {
            if (!messagesFile.exists()) {
                overwriteMessagesFileWithDefaultValues()
            } else {
                logger.info("The file messages.txt was loaded")
            }
        } catch (e: Exception) {
            logger.error(e)
            logger.error("An error has occurred while checking the messages.txt")
            logger.info("Trying to replace it by default...")
            overwriteMessagesFileWithDefaultValues()
        }
    }

    private fun overwriteMessagesFileWithDefaultValues() {
        try {
            val fw = FileWriter(messagesFile)
            val bw = BufferedWriter(fw)
            bw.write("Join message:")
            bw.newLine()
            bw.write("&e%playername joined the game")
            bw.newLine()
            bw.newLine()
            bw.write("Quit message:")
            bw.newLine()
            bw.write("&e%playername left the game")
            bw.newLine()
            bw.newLine()
            bw.write("How to write own messages:")
            bw.newLine()
            bw.write("Visit http://dev.bukkit.org/bukkit-plugins/personal-login-message/pages/standard-mode/")
            bw.newLine()
            bw.write("NOTE: Please don't move the lines. Otherwise the plugin will return wrong values!!")
            bw.close()
        } catch (e: Exception) {
            logger.error("Editing 'messages.txt' was not possible! Check the plugin's folder")
        }
    }

    companion object {
        private val logger = PLM.logger()
    }
}