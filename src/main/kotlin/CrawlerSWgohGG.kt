package com.tsepesh.thoma

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.IOException
import java.net.ConnectException
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger


class CrawlerSWgohGG {

    companion object {
        private var linkCounter = AtomicInteger(0)
        private const val maxAttempts = 5

        suspend fun getPlayers(allyCode: UInt): MutableList<Player> {

            val playerList = mutableListOf<Player>()
            var url = "https://swgoh.gg/p/$allyCode"
            val coroutines = mutableListOf<Job>()
            var attempt = 0
            while (attempt < maxAttempts) {
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
                            val coroutine = CoroutineScope(Dispatchers.IO).launch {
                                val allyCode = (Regex("\\D")).replace(row.select("a").attr("href"), "").toInt()
                                if (playerName.isNotEmpty()) {
                                    val chars = getAllChars(allyCode.toUInt())
                                    val stats = getPlayerStats(allyCode.toUInt())
                                    val player = Player(playerName, allyCode, stats, chars)
                                    println(player)
                                    synchronized(playerList) {
                                        playerList.add(player)
                                    }
                                }
                            }
                            coroutines.add(coroutine)
                        }
                    }
                    break
                } catch (e: ConnectException) {
                    attempt++
                    println("getPlayers attempt= $attempt")
                    if (attempt < maxAttempts) {
                        delay(10000)
                    } else {
                        throw e
                    }
                }
            }
            coroutines.joinAll()
            return playerList
        }

        private suspend fun getAllChars(allyCode: UInt): List<Character> {
            val charsList = ConcurrentLinkedQueue<Character>()
            var counter = 1
            val attempt = 0
            val semaphore = Semaphore(25)
            while (attempt < maxAttempts) {
                try {
                    val charDoc = Jsoup.connect("https://swgoh.gg/p/$allyCode/characters/").get()
                    while (true) {
                        val charDiv = charDoc.select(
                            "body > div.container.p-t-md > div.content-container > div.content-container-primary.character-list.char-search >" +
                                    " ul > li.media.list-group-item.p-a.collection-char-list > div > div:nth-child($counter) >" +
                                    " div > div.character-portrait.character-portrait--size-normal > a"
                        ).attr("href")
                        if (charDiv.isEmpty()) {
                            break
                        }

                        counter += 1
                        //println("\n$charDiv")
                        semaphore.acquire()
                        try {
                            withContext(Dispatchers.IO) {
                                linkCounter.incrementAndGet()
                                val character = getCharStats(allyCode, charDiv.substringAfterLast("/"))
                                charsList.add(character)
                                delay(100)
                            }
                        } catch (e: Exception) {
                            println(e.message)
                        } finally {
                            semaphore.release()
                        }
                    }
                    break
                } catch (e: HttpStatusException) {
                    println("Failed to connect to URL")
                    println(e.statusCode)
                    println(e.message)
                } catch (e: IOException) {
                    println("charStat IOException $attempt")
                    println(e.message)
                } catch (e: ConnectException) {
                    println("charStat attempt $attempt")
                    println(e.message)
                }
            }
            return charsList.toList()
        }


        suspend fun getCharStats(allyCode: UInt, charName: String): Character {
            val url = "https://swgoh.gg/p/${allyCode.toInt()}/characters/$charName"

            val character: Character
            var stars = 0
            var gear = "0"
            var omic = 0
            var zeta = 0
            var attempt = 0
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

                    println("player character link=$url  counter=${linkCounter.get()}")

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
            //println(character)
            return character
        }

        private suspend fun getPlayerStats(allyCode: UInt): Stats {
            val url = "https://swgoh.gg/p/${allyCode.toInt()}"
            val doc = Jsoup.connect(url).get()
            val omics = doc.select(
                "body > div.container.p-t-md > div.content-container >" +
                        " div.content-container-primary.character-list > ul > li:nth-child(3) > div > div > ul > li:nth-child(3) > h5 > a"
            ).text().toInt()
            val zetas = doc.select(
                "body > div.container.p-t-md > div.content-container >" +
                        " div.content-container-primary.character-list > ul > li:nth-child(3) > div > div > ul > li:nth-child(2) > h5 > a"
            ).text().toInt()
            return Stats(omics, zetas)

        }
//        suspend fun getNextZeta(allyCode: UInt){
//            val url = "https://swgoh.gg/p/${allyCode.toInt()}/what-to-zeta/"
//            val doc = Jsoup.connect(url).get()
//            var counter = 5
//
//            while (true) {
//                val  = .select(
//                    counter
//                )
//                    .attr("href")
//                if (.isEmpty()) {
//                    break
//                }
//                counter += 1
//                println("\n$")
//            }
//        }
    }
}