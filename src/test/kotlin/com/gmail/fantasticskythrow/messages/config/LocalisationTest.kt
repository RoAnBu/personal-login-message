package com.gmail.fantasticskythrow.messages.config

import com.gmail.fantasticskythrow.configuration.Localisation
import com.gmail.fantasticskythrow.configuration.LocalisationFile
import com.gmail.fantasticskythrow.configuration.TimeNames
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

@ExtendWith(MockKExtension::class)
class LocalisationTest {

    private val file = File("src/test/resources/localisation1.yml")
    private val file2 = File("src/test/resources/localisation2.yml")

    @Test
    fun `loading and parsing of file should work`() {
        LocalisationFile(file)
    }

    @Test
    fun `alternate country name, contained in yaml, should return correct alternate name`() {
        val result = Localisation(LocalisationFile(file).yamlConfiguration).getAlternateNameForCountry("Germany")

        Assertions.assertEquals("Deutschland", result)
    }

    @Test
    fun `alternate country name, not contained in yaml, should return same value as argument`() {
        val result = Localisation(LocalisationFile(file).yamlConfiguration).getAlternateNameForCountry("Kyrgyzstan")

        Assertions.assertEquals("Kyrgyzstan", result)
    }

    @Test
    fun `TimeNames, should return configured time names`() {
        val result = Localisation(LocalisationFile(file).yamlConfiguration).timeNames

        val expected = TimeNames("秒", "秒", "分", "分", "時間", "時間",
                "日", "日", "ヶ月", "ヶ月", "見たことがない")
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `TimeNames, months missing, should return default english names`() {
        val result = Localisation(LocalisationFile(file2).yamlConfiguration).timeNames

        val expected = TimeNames.createEnglishTimeNames()
        Assertions.assertEquals(expected, result)
    }
}