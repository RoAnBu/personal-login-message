package com.gmail.fantasticskythrow.configuration

interface IAppConfiguration {
    val pluginEnabled: Boolean
    val usePermissions: Boolean
    val usePermissionsForPublicMessages: Boolean
    val useFakeJoinMsg: Boolean
    val useFakeQuitMsg: Boolean
    val replaceVnpFakeMsg: Boolean
    val advancedStatusEnabled: Boolean
    val useEssentialsNickName: Boolean
    val welcomeMessagesDelayMs: Int
}