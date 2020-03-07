package com.gmail.fantasticskythrow.messages.generator

import com.gmail.fantasticskythrow.messages.data.MessageData
import org.bukkit.entity.Player

interface IBasicMessageGenerator {
    fun getJoinMessageDataForPlayer(player: Player): MessageData
    fun getQuitMessageDataForPlayer(player: Player): MessageData
}