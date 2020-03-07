package com.gmail.fantasticskythrow.other.plugins

import com.maxmind.geoip.Country
import uk.org.whoami.geoip.GeoIPLookup
import java.net.InetAddress

class IPLookupWrapper(private val ipLookup: GeoIPLookup): IIPAddressLookup {
    override fun getCountry(inet: InetAddress): Country {
        return ipLookup.getCountry(inet)
    }
}