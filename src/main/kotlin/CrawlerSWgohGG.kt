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
        const val maxAttempts = 5

        suspend fun getPlayers(allyCode: UInt): MutableList<Player> {
            val playerList = mutableListOf<Player>()
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val coroutines = mutableListOf<Job>()
            var url = "https://swgoh.gg/p/$allyCode"
            var attempt = 0

            while (attempt < maxAttempts) {
                try {
                    var doc = Jsoup.connect(url).get()
                    url = "https://swgoh.gg${
                        doc.select("body > div.global-container > header.global-page-header.mb-4 > div > div.d-flex.col-gap-4.align-items-center > div.d-flex.col-gap-1 > div.flex-1.align-self-center > a")
                            .attr("href")
                    }"
                    println("Guild link= $url")
                    doc = Jsoup.connect(url).get()
                    val table =
                        doc.select("body > div.global-container > main > div > div > div.l-sidebar__main > div.paper.mt-2 > div > table > tbody")
                            .first()
                    var playerIterator = 1

                    while (playerIterator <= 50) {
                        val player = table!!.select("tr:nth-child($playerIterator)")
                        if (player.isEmpty()) {
                            println("break")
                            break
                        }
                        val playerName =
                            player.select("td:nth-child(1) > a > div > div.flex-1.lh-sm > div.fw-bold.text-white")
                                .text()
                        if (playerName.isNotEmpty()) {
                            val coroutine = coroutineScope.launch {
                                println(player.select("a").attr("href"))
                                val allyCode = (Regex("\\D")).replace(player.select("a").attr("href"), "").toInt()
                                val stats = getPlayerStats(allyCode.toUInt())
                                val chars = getAllChars(allyCode.toUInt())
                                val player = Player(playerName, allyCode, stats, chars)
                                synchronized(playerList) {
                                    playerList.add(player)
                                }
                            }
                            coroutines.add(coroutine)
                        }
                        playerIterator += 1
                    }
                    break
                } catch (e: ConnectException) {
                    attempt++
                    println("getPlayers attempt= $attempt")
                    if (attempt < maxAttempts) {
                        delay(1000)
                    } else {
                        throw e
                    }
                } catch (e: HttpStatusException){
                    attempt++
                    println(e.statusCode)
                    println("getPlayers attempt= $attempt")
                    if (attempt < maxAttempts) {
                        delay(1000)
                    } else{
                        throw e
                    }
                }
            }
            coroutines.joinAll()
            return playerList
        }

        private suspend fun getAllChars(allyCode: UInt): List<Character> {
            val charsList = ConcurrentLinkedQueue<Character>()
            val charsNameList = mutableListOf<String>()
            val attempt = 0
            val semaphore = Semaphore(15)

            while (attempt < maxAttempts) {
                try {
                    val charDoc = Jsoup.connect("https://swgoh.gg/p/$allyCode/characters/").get()
                    var counter = 1
                    while (true) {
                        val charDiv = charDoc.select(
                            "body > div.global-container > main > div > div > div.l-sidebar__main > div.js-unit-search > " +
                                    "div.js-unit-search__results.unit-card-grid.mt-2 > div:nth-child($counter) > a"
                        ).attr("href")
                        if (charDiv.isEmpty()) {
                            println("char count - $counter")
                            break
                        }
                        charsNameList.add(charDiv.substringBeforeLast("/").substringAfterLast("/"))
                        counter++
                    }

                        var semaphoreCounter = 0
                        coroutineScope {
                            val jobs = mutableListOf<Job>()
                            for (name in charsNameList) {
                                jobs.add(launch {
                                    semaphore.acquire()
                                    semaphoreCounter++
                                    try {
                                        withContext(Dispatchers.IO) {
                                            linkCounter.incrementAndGet()
                                            val character = getCharStats(allyCode, name)
                                            charsList.add(character)
                                            delay(400)
                                        }
                                    } catch (e: Exception) {
                                        println("Error in coroutine for character $name: ${e.message}")
                                    } finally {
                                        //println("Releasing semaphore for character $name")
                                        semaphoreCounter--
                                        //println("semaphoreCounter = $semaphoreCounter")
                                        semaphore.release()
                                    }
                                })
                            }
                            jobs.joinAll()
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
            val url = "https://swgoh.gg/p/${allyCode.toInt()}/unit/$charName"

            val character: Character
            var stars = 0
            var charGP = 0
            var gear = "0"
            var omic = 0
            var zeta = 0
            var attempt = 0
            while (attempt < maxAttempts) {
                try {
                    val doc = Jsoup.connect(url).get()

                    stars = doc.select("div.rarity-range__star:not(.rarity-range__star--inactive)").size
//                    println("\nstars $stars")

                    charGP = doc.select(
                        "div.l-sidebar__main > div.row.g-2" +
                                " > div.col-12.col-lg-4 > div > div:nth-child(2) > div > div:nth-child(1) > div > div.stat-table-data__entry-primary-value"
                    ).text().replace(",", "").toInt()
//                    println("gp $charGP")

                    var gearCounter = 1

                    otherLoop@ while (true) {

                        while (doc.select(
                                "div.l-sidebar__main > div.row.g-2 > div.col-12.col-lg-8 > div:nth-child(4)" +
                                        " > div:nth-child(2) > div > div.flex-1.align-self-start.ms-4 > h3"
                            )
                                .text() == "Gear Remaining" && gearCounter <= 12
                        ) {
                            if (doc.select(
                                    "main > div > div > div.l-sidebar__main > div.row.g-2 > div.col-12.col-lg-8 > div:nth-child(4) > div:nth-child(2)" +
                                            " > div > div.w-240px > div > div.unit-gear__heading.unit-gear__heading--t$gearCounter"
                                ).size >= 1
                            ) {
                                gear = doc.select(
                                    "main > div > div > div.l-sidebar__main > div.row.g-2 > div.col-12.col-lg-8 > div:nth-child(4) > div:nth-child(2)" +
                                            " > div > div.w-240px > div > div.unit-gear__heading.unit-gear__heading--t$gearCounter"
                                ).text()
                                break@otherLoop
                            } else {
                                gear = "-"
                            }
                            gearCounter++
                        }

                        gear = doc.select(
                            "div.l-sidebar__main > div.row.g-2 > div.col-12.col-lg-8" +
                                    " > div:nth-child(4) > div:nth-child(2) > div > div > div.unit-relic-icon__backdrop > div.unit-relic-icon__tier > svg > text"
                        ).text()
                        break


                    }
                    //println("gear $gear")

                    val omicPotrait = doc.select(
                        "div.character-portrait__primary > div.character-portrait__omicron > svg > text"
                    ).text()

                    omic = if (omicPotrait.isNotEmpty()) {
                        omicPotrait.toInt()
                    } else {
                        0
                    }
                    //println("omic $omic")

                    val zetaPotrait = doc.select(
                        "header.global-page-header.mb-4 > div > div.d-flex.align-items-center.col-gap-3.mt-3 > " +
                                "div.w-64px > div > div > div.character-portrait__primary > div.character-portrait__zeta > svg > text"
                    ).text()
                    zeta = if (zetaPotrait.isNotEmpty()) {
                        zetaPotrait.toInt()
                    } else {
                        0
                    }
                    //println("zeta $zeta")
                    println("player character link=$url  counter=${linkCounter.get()}")
                    break
                } catch (e: HttpStatusException) {
                    println("Failed to connect to URL:$url")
                    println(e.statusCode)
                    if (e.statusCode == 404) {
                        return Character(charName, stars, charGP, gear, omic, zeta)
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


            character = Character(charName, stars, charGP, gear, omic, zeta)
            //println(character)
            return character
        }

        private suspend fun getPlayerStats(allyCode: UInt): Stats {
            val url = "https://swgoh.gg/p/${allyCode.toInt()}"
            val doc = Jsoup.connect(url).get()
            val omics = doc.select(
                "body > div.global-container > main > div > div > div.l-sidebar__main > div:nth-child(2) > div.row.g-3 > div:nth-child(4) > div.fw-bold.fs-2"
            ).text().toInt()
            val zetas = doc.select(
                "body > div.global-container > main > div > div > div.l-sidebar__main > div:nth-child(2) > div.row.g-3 > div:nth-child(3) > div.fw-bold.fs-2"
            ).text().toInt()
            return Stats(omics, zetas)
//            return Stats(0, 0)
        }

        suspend fun getCharsAlly(allyCode: UInt): List<Character> {
            return getAllChars(allyCode)
        }
    }
}
