package com.tsepesh.thoma

import kotlinx.serialization.json.Json
import java.io.File

class ConverterJsonToCsv {

    companion object {

        fun convertChar(jsonFilePath: String, charName: String): String {
            val json = File(jsonFilePath).readText()
            val players = Json.decodeFromString<List<Player>>(json)

            val csv = buildString {
                appendLine("name,allyCode,idName,stars,gear,omic,zeta")
                players.forEach { player ->
                    val char: Character? = player.chars.find { c -> c.idName == charName }
                    if (char != null) {
                        appendLine("${player.name},${player.allyCode},${char.idName},${char.stars},${char.gear},${char.omic},${char.zeta}")
                    }
                }
            }
            return csv
        }

        fun convertTeam(jsonFilePath: String, team: Team): String {
            val json = File(jsonFilePath).readText()
            val players = Json.decodeFromString<List<Player>>(json)
            val csv = buildString {
                appendLine(", , ")
                appendLine("name,allyCode,${team.charList.flatMap { listOf("stars", "gear", "omic", "zeta") }.joinToString(",")}")
                players.forEach { player ->
                    val charStats = team.charList.joinToString(",") { charName ->
                        val char: Character? = player.chars.find { c -> c.idName == charName }
                        char?.getCharStats() ?: ",,,"
                    }
                    appendLine("${player.name},${player.allyCode},${charStats}")
                }
            }
            return csv
        }

    }
}