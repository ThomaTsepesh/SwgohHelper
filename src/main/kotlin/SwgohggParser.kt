package com.tsepesh.thoma

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
fun main() {

//    val playerList = getPlayers(849418263u)
//    val charStats = getCharStats(playerList, "ahsoka-tano-fulcrum")

//    val jsonPlayer = Json.encodeToString(playerList)
//    File("PlayerListSeria.json").writeText(jsonPlayer)

//    val jsonCharStat = Json.encodeToString(charStats)
//    File("CharStatsSeria.json").writeText(jsonCharStat)


}

private fun getPlayers(allyCode: UInt): MutableList<Player> {

    var url = "https://swgoh.gg/p/$allyCode"
    var doc = Jsoup.connect(url).get()
    url = "https://swgoh.gg${doc.select("body > div.container.p-t-md > div.content-container > div.content-container-aside >" +
            " div.panel.panel-default.panel-profile.m-b-sm > div.panel-body > p:nth-child(3) > strong > a").attr("href")}"
    println(url)
    doc = Jsoup.connect(url).get()
    val table = doc.select("body > div.container.p-t-md > div.content-container > div.content-container-primary.character-list > ul > li.media.list-group-item.p-0.b-t-0 > div > table").first()
    val rows = table!!.select("tr")
    val playerList = mutableListOf<Player>()

    for (row in rows) {
        val playerName = row.select("td").attr("data-sort-value")
        val playerLink = row.select("a").attr("href")
        val allyCode = (Regex("\\D")).replace(playerLink, "")
        if (playerName.isNotEmpty()){
            val player = Player(playerName, allyCode.toInt(), playerLink)
            println(player)
            playerList.add(player)
        }
    }
    return playerList
}

fun getCharStats(playerList: MutableList<Player>, charName:String): List<Character> {
    var url: String
    val charsList = mutableListOf<Character>()
    for (player in playerList) {
        url = "https://swgoh.gg${player.playerLink}characters/$charName"
        var stars = 0
        var gear = "0"
        var omic = 0
        var zeta = 0
        try {
            val doc = Jsoup.connect(url).get()

            stars = when(doc.select("body > div.container.p-t-md > div.content-container > div.content-container-primary > ul:nth-child(1) > li > div > div:nth-child(4)" +
                    " > div:nth-child(4) > span > span.unit-gp-stat-amount-value.unit-gp-stat-amount-current").text()){
                "4,655" -> 7
                "2660" -> 6
                "1520" -> 5
                "1013" -> 4
                "675" -> 3
                "450" -> 2
                else -> 1
            }
            gear = if (doc.select("body > div.container.p-t-md > div.content-container > div.content-container-aside > ul > li > h5").text().equals("Current Gear")) {
                doc.select("body > div.container.p-t-md > div.content-container > div.content-container-aside > ul > li > div > a > div.pc-heading")
                    .text()
            }else{
                doc.select("body > div.container.p-t-md > div.content-container > div.content-container-aside > ul > li > div > div > div.relic-portrait__backdrop > div")
                    .text()
            }
            val omicPotrait = doc.select(
                "body > div.container.p-t-md > div.content-container > div.content-container-aside > div.panel.panel-default.panel-profile.m-b-sm >" +
                        " div.panel-body.text-center > div.pc-portrait > div > a > div.character-portrait__primary.character-portrait__primary--size-large >" +
                        " div.character-portrait__omicron.character-portrait__omicron--size-large > span"
            ).text()
            omic = if (omicPotrait.isNotEmpty()
            ){
                omicPotrait.toInt()
            } else {
                0
            }
            val zetaPotrait = doc.select("body > div.container.p-t-md > div.content-container > div.content-container-aside > div.panel.panel-default.panel-profile.m-b-sm >" +
                    " div.panel-body.text-center > div.pc-portrait > div > a > div.character-portrait__primary.character-portrait__primary--size-large >" +
                    " div.character-portrait__zeta.character-portrait__zeta--size-large")
            zeta = if(zetaPotrait.text().isNotEmpty()){
                zetaPotrait.text().toInt()
            }else{
                0
            }

            println("URL=$url gear=$gear")

        }catch (e: HttpStatusException){
            println("Failed to connect to URL:$url")
        }
        val character = Character(charName, stars, gear, omic, zeta)
        println(character)
        charsList.add(character)
    }
    return charsList
}

class SwgohggParser {

}