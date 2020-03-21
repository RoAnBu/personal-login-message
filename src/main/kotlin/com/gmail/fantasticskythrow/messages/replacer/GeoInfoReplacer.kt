package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.PLM
import com.gmail.fantasticskythrow.configuration.ICountryAlternateNames
import com.gmail.fantasticskythrow.other.plugins.IIPAddressLookup
import org.bukkit.entity.Player

class GeoInfoReplacer(private val ipLookup: IIPAddressLookup?,
                      private val countryAlternates: ICountryAlternateNames?): IPlaceholderReplacer {
    private val logger = PLM.logger()

    override fun replacePlaceholders(message: String, player: Player, isQuitting: Boolean): String {
        var modMessage = message
        modMessage = getReplacedIP(modMessage, player)
        modMessage = getReplacedCountry(modMessage, player)
        return modMessage
    }

    /**
     * Replaces %IP by the player's address. The / in front of it will be deleted
     * @param inputText The text which could contain %IP
     * @param player The concerning player
     * @return The modified string
     */
    private fun getReplacedIP(inputText: String, player: Player): String {
        var text = inputText
        if (text.contains("%IP")) {
            val formattedIPAddress = player.address
                    .toString()
                    .replace("/".toRegex(), "")
                    .substringBefore(':')
            text = text.replace("%IP", formattedIPAddress)
        }
        return text
    }

    /**
     * Replaces %country with the country name the player joined from. Changed country names come from plmfile
     * @param inputText the string which can contain %country
     * @param player the player who joined (getting is address)
     * @return replaced %country if possible. Otherwise it will return unknown
     */
    private fun getReplacedCountry(inputText: String, player: Player): String {
        var text = inputText
        return if (text.contains("%country")) {
            val geoIP = ipLookup
            if (geoIP != null) {
                var country: String = geoIP.getCountry(player.address!!.address)
                countryAlternates?.let { country = it.getAlternateNameForCountry(country) }
                if (country.equals("N/A", ignoreCase = true)) {
                    country = "local network"
                }
                text = text.replace("%country", country)
                text
            } else {
                logger.warn("You used %country but GeoIPTools is not installed or no database is initialized")
                logger.warn("Use /geoupdate if it's installed")
                text = text.replace("%country", "unknown")
                text
            }
        } else {
            text
        }
    }
}