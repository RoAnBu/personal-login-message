package com.gmail.fantasticskythrow.other.plugins

import com.maxmind.geoip.Country
import java.net.InetAddress

interface IIPAddressLookup {
    fun getCountry(inet: InetAddress): Country
}