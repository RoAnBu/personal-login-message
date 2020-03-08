package com.gmail.fantasticskythrow.configuration

interface IAppConfiguration {
    val pluginEnabled: Boolean
    val usePermGeneral: Boolean
    val usePermPM: Boolean
    val useFakeJoinMsg: Boolean
    val useFakeQuitMsg: Boolean
    val replaceVnpFakeMsg: Boolean
    val advancedStatus: Boolean
    val useEssentialsNick: Boolean
    val timeNames: TimeNames?
    val delay: Int
}