package com.tsepesh.thoma

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.net.ConnectException

@OptIn(ExperimentalSerializationApi::class)
suspend fun main() {
    val dataPath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\data\\"
    val playerList = getPlayers(849418263u)
//    val charStats = getCharStats(849418263u, "general-grievous")
//    println(charStats)

//    println(getAllChars(849418263u))
//    val jsonAllChars = Json.encodeToString(getAllChars(849418263u))
//    File("AllCharsList.json").writeText(jsonAllChars)

    val jsonPlayer = Json.encodeToString(playerList)
    File("PlayerList.json").writeText(jsonPlayer)
//
//    val jsonCharStat = Json.encodeToString(charStats)
//    File("CharStatsSeria.json").writeText(jsonCharStat)

//    val allCharsplr = getAllChars(849418263u)
 //   val jsonCharStat = Json.encodeToString(allCharsplr)
//    File("${dataPath}plrstatchar.json").writeText(jsonCharStat)
}

private suspend fun getPlayers(allyCode: UInt): MutableList<Player> {


    val playerList = mutableListOf<Player>()
    var url = "https://swgoh.gg/p/$allyCode"
    var attempt = 0
    val maxAttempt = 5
    while (attempt < maxAttempt) {
        try {
            var doc = Jsoup.connect(url).get()
            url = "https://swgoh.gg${
                doc.select(
                    "body > div.container.p-t-md > div.content-container > div.content-container-aside >" +
                            " div.panel.panel-default.panel-profile.m-b-sm > div.panel-body > p:nth-child(3) > strong > a"
                ).attr("href")
            }"
            println("Guild link= $url")
            doc = Jsoup.connect(url).get()
            val table = doc.select(
                "body > div.container.p-t-md > div.content-container > div.content-container-primary.character-list >" +
                        " ul > li.media.list-group-item.p-0.b-t-0 > div > table"
            ).first()
            val rows = table!!.select("tr")

            for (row in rows) {
                val playerName = row.select("td").attr("data-sort-value")
                if (playerName.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val allyCode = (Regex("\\D")).replace(row.select("a").attr("href"), "").toInt()
                        if (playerName.isNotEmpty()) {
                            val chars = getAllChars(allyCode.toUInt())
                            val player = Player(playerName, allyCode, chars)
                            println(player)
                            synchronized(playerList) {
                                playerList.add(player)
                            }
                        }
                    }
                }
            }
            break
        } catch (e: ConnectException) { attempt++
            println("getPlayers attempt= $attempt")
            if (attempt < maxAttempt) {
                delay(10000)
            } else {
                throw e
            }
        }
    }
    delay(780000)
    return playerList
}

suspend fun getAllChars(allyCode: UInt): List<Character>{

    var charsList = mutableListOf<Character>()
    var counter = 1

    var attempt = 0
    val maxAttempts = 5
    while (attempt < maxAttempts) {
        try {
            val charDoc = Jsoup.connect("https://swgoh.gg/p/$allyCode/characters/").get()
            while (true) {
                val charDiv = charDoc.select(
                    "body > div.container.p-t-md > div.content-container > div.content-container-primary.character-list.char-search >" +
                            " ul > li.media.list-group-item.p-a.collection-char-list > div > div:nth-child($counter) >" +
                            " div > div.character-portrait.character-portrait--size-normal > a"
                )
                    .attr("href")
                if (charDiv.isEmpty()) {
                    break
                }
                counter += 1
                println("\n$charDiv")
                charsList.add(getCharStats(allyCode, charDiv.substringAfterLast("/")))
            }

            break
        } catch (e: ConnectException) { attempt++
            println("getChars attempt = $attempt")
            if (attempt < maxAttempts) {
                delay(10000)
            } else {
                throw e
            }
        }

    }

    return charsList
}

suspend fun getCharStats(allyCode: UInt, charName: String): Character {
    val url = "https://swgoh.gg/p/${allyCode.toInt()}/characters/$charName"

    val character: Character
    var stars = 0
    var gear = "0"
    var omic = 0
    var zeta = 0
    var attempt = 0
    val maxAttempts = 5
    while (attempt < maxAttempts) {
        try {
            val doc = Jsoup.connect(url).get()
            stars = when (doc.select(
                "body > div.container.p-t-md > div.content-container > div.content-container-primary > ul:nth-child(1) > li > div > div:nth-child(4)" + " > div:nth-child(4) > span > span.unit-gp-stat-amount-value.unit-gp-stat-amount-current"
            ).text()) {
                "4,655" -> 7
                "2660" -> 6
                "1520" -> 5
                "1013" -> 4
                "675" -> 3
                "450" -> 2
                else -> 1
            }
            gear =
                if (doc.select("body > div.container.p-t-md > div.content-container > div.content-container-aside > ul > li > h5")
                        .text().equals("Current Gear")
                ) {
                    doc.select("body > div.container.p-t-md > div.content-container > div.content-container-aside > ul > li > div > a > div.pc-heading")
                        .text()
                } else {
                    doc.select("body > div.container.p-t-md > div.content-container > div.content-container-aside > ul > li > div > div > div.relic-portrait__backdrop > div")
                        .text()
                }
            val omicPotrait = doc.select(
                "body > div.container.p-t-md > div.content-container > div.content-container-aside > div.panel.panel-default.panel-profile.m-b-sm >" + " div.panel-body.text-center > div.pc-portrait > div > a > div.character-portrait__primary.character-portrait__primary--size-large >" + " div.character-portrait__omicron.character-portrait__omicron--size-large > span"
            ).text()
            omic = if (omicPotrait.isNotEmpty()) {
                omicPotrait.toInt()
            } else {
                0
            }
            val zetaPotrait = doc.select(
                "body > div.container.p-t-md > div.content-container > div.content-container-aside > div.panel.panel-default.panel-profile.m-b-sm >" + " div.panel-body.text-center > div.pc-portrait > div > a > div.character-portrait__primary.character-portrait__primary--size-large >" + " div.character-portrait__zeta.character-portrait__zeta--size-large"
            )
            zeta = if (zetaPotrait.text().isNotEmpty()) {
                zetaPotrait.text().toInt()
            } else {
                0
            }

            println("player character link=$url")

            break
        } catch (e: HttpStatusException) {
            println("Failed to connect to URL:$url")
            println(e.statusCode)
            if (e.statusCode == 404) {
                return Character(charName, stars, gear, omic, zeta)
            }
        } catch (e: IOException) {
            attempt++
            println("charStat IOException $attempt")
            if (attempt < maxAttempts) {
                delay(10000)
            } else {
                throw e
            }
        } catch (e: ConnectException) {
            attempt++
            println("charStat attempt $attempt")
            if (attempt < maxAttempts) {
                delay(10000)
            } else {
                throw e
            }
        }
    }


    character = Character(charName, stars, gear, omic, zeta)
    println(character)
    return character
}

class SwgohggParser {

}