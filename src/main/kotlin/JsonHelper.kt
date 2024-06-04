package com.tsepesh.thoma

import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class JsonHelper {
    companion object {

        fun parseTeam(dataPath: String, team: Team): List<List<String>> {
            val players = if (Files.exists(Paths.get(dataPath))) {
                Json.decodeFromString<List<Player>>(File(dataPath).readText())
            } else {
                println("Data not found")
                return emptyList()
            }
            val result = mutableListOf<List<String>>()
            result.add(listOf("", "", ""))
            result.add(listOf("", "", ""))
            for (player in players) {
                val charStats = mutableListOf(player.name, player.allyCode.toString())
                var totalGP = 0
                team.charList.forEach { charName ->
                    val char: Character? = player.chars.find { c -> c.idName == charName }
                    if (char != null) {
                        charStats.addAll(
                            listOf(
                                char.stars.toString(),
                                char.charGP.toString(),
                                char.gear,
                                char.omic.toString(),
                                char.zeta.toString()
                            )
                        )
                        totalGP += char.charGP
                    }
                }
                charStats.add(2, totalGP.toString())
                result.add(charStats)
            }
            return result.sortedBy { it.firstOrNull() ?: "" }
        }

        fun parseChar(dataPath: String, charName: String): List<List<String>> {
            val players = if (Files.exists(Paths.get(dataPath))) {
                Json.decodeFromString<List<Player>>(File(dataPath).readText())
            } else {
                println("Data not found")
                return emptyList()
            }

            val result = mutableListOf<List<String>>()
            result.add(listOf("", "", ""))
            val title = mutableListOf("name", "allyCode", "stars", "charGP", "gear", "omic", "zeta")
            result.add(title)

            players.forEach { player ->

                val charStats = mutableListOf(player.name, player.allyCode.toString())
                val char: Character? = player.chars.find { c -> c.idName == charName }
                if (char != null) {
                    charStats.addAll(
                        listOf(
                            char.stars.toString(),
                            char.charGP.toString(),
                            char.gear,
                            char.omic.toString(),
                            char.zeta.toString()
                        )
                    )
                }
                result.add(charStats)
            }
            return result
        }

        fun parsePlayers(dataPath: String): List<List<String>> {
            val json = File(dataPath).readText()
            val players = Json.decodeFromString<List<Player>>(json)
            val result = mutableListOf<List<String>>()
            result.addAll(listOf(listOf("name", "allycode")))
                players.forEach{ player ->
                    result.add(listOf(player.name, player.allyCode.toString()))
                }
            return result
        }

    }
}