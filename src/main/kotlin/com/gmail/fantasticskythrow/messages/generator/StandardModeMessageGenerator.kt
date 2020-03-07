package com.gmail.fantasticskythrow.messages.generator

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.messages.config.IStandardMessagesFile
import com.gmail.fantasticskythrow.messages.config.StandardMessagesFile
import com.gmail.fantasticskythrow.messages.data.MessageData
import com.gmail.fantasticskythrow.messages.replacer.IPlaceholderReplacer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.io.File

class StandardModeMessageGenerator(plm: PLM,
                                   private val standardMessagesFile: IStandardMessagesFile = StandardMessagesFile(File(plm.dataFolder, "messages.txt")),
                                   private val placeholderReplacer: IPlaceholderReplacer
) : IBasicMessageGenerator {

    override fun getJoinMessageDataForPlayer(player: Player): MessageData {
        var message = standardMessagesFile.joinMessage
        message = placeholderReplacer.replacePlaceholders(message, player)
        message = ChatColor.translateAlternateColorCodes('&', message)
        return MessageData(message)
    }

    override fun getQuitMessageDataForPlayer(player: Player): MessageData {
        var message = standardMessagesFile.quitMessage
        message = placeholderReplacer.replacePlaceholders(message, player)
        message = ChatColor.translateAlternateColorCodes('&', message)
        return MessageData(message)
    }

    fun reload() {
        standardMessagesFile.reload()
    }
}