package com.gmail.fantasticskythrow.messages.replacer

import com.gmail.fantasticskythrow.configuration.ICountryAlternateNames
import com.gmail.fantasticskythrow.other.plugins.IIPAddressLookup
import com.maxmind.geoip.Country
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.InetSocketAddress

@ExtendWith(MockKExtension::class)
class GeoInfoReplacerTest {

    @MockK
    lateinit var ipLookup: IIPAddressLookup
    @MockK
    lateinit var countryAlternateNames: ICountryAlternateNames
    @MockK
    lateinit var player: Player

    @Test
    fun `message should stay unchanged with no placeholders`() {
        val message = "This is a test message"

        var geoInfoReplacer = geoReplacerWithNoArgs()
        var result = geoInfoReplacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)

        geoInfoReplacer = geoReplacerWithIPLookup()
        result = geoInfoReplacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)

        geoInfoReplacer = GeoInfoReplacer(null, countryAlternateNames)
        result = geoInfoReplacer.replacePlaceholders(message, player)
        Assertions.assertEquals(message, result)

    }

    private fun geoReplacerWithNoArgs() = GeoInfoReplacer(null, null)
    private fun geoReplacerWithIPLookup() = GeoInfoReplacer(ipLookup, null)
    private fun geoReplacerWithBothArgs() = GeoInfoReplacer(ipLookup, countryAlternateNames)

    @Test
    fun `replace %IP with no dependencies provided`() {
        val replacer = geoReplacerWithNoArgs()
        val message = "Player joined with IP %IP"

        every { player.address } returns InetSocketAddress("13.32.10.149", 123)

        val result = replacer.replacePlaceholders(message, player)

        val expected = "Player joined with IP 13.32.10.149"

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `replace %country with country name without alternate country names`() {
        val replacer = geoReplacerWithIPLookup()
        val message = "Player joined with IP %IP from %country"

        val address = InetSocketAddress("13.32.10.149", 123)

        every { player.address } returns address
        every { ipLookup.getCountry(address.address) } returns Country("FR", "France")

        val result = replacer.replacePlaceholders(message, player)

        val expected = "Player joined with IP 13.32.10.149 from France"

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `replace %country with country name with alternate country names but no alternate provided`() {
        val replacer = geoReplacerWithBothArgs()
        val message = "Player joined with IP %IP from %country"

        val address = InetSocketAddress("13.32.10.149", 123)

        every { player.address } returns address
        every { ipLookup.getCountry(address.address) } returns Country("FR", "France")
        every { countryAlternateNames.getAlternateNameForCountry("France") } returns "France"

        val result = replacer.replacePlaceholders(message, player)

        val expected = "Player joined with IP 13.32.10.149 from France"

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `replace %country without iplookup provided`() {
        val replacer = geoReplacerWithNoArgs()
        val message = "Player joined with IP %IP from %country"

        val address = InetSocketAddress("13.32.10.149", 123)

        every { player.address } returns address

        val result = replacer.replacePlaceholders(message, player)

        val expected = "Player joined with IP 13.32.10.149 from unknown"

        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `replace %country with country name with alternate country names with alternate provided`() {
        val replacer = geoReplacerWithBothArgs()
        val message = "Player joined with IP %IP from %country"

        val address = InetSocketAddress("13.32.10.149", 123)

        every { player.address } returns address
        every { ipLookup.getCountry(address.address) } returns Country("FR", "France")
        every { countryAlternateNames.getAlternateNameForCountry("France") } returns "Frankreich"

        val result = replacer.replacePlaceholders(message, player)

        val expected = "Player joined with IP 13.32.10.149 from Frankreich"

        Assertions.assertEquals(expected, result)
    }

}