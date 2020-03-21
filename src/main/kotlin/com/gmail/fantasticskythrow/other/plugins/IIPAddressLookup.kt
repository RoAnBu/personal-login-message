package com.gmail.fantasticskythrow.other.plugins

import java.net.InetAddress

interface IIPAddressLookup {
    fun getCountry(inet: InetAddress): String
}