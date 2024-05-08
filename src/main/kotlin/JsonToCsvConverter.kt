package com.tsepesh.thoma

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class JsonToCsvConverter {

    companion object {

        fun convertChar(jsonFilePath: String, charName: String): String {
            val json = File(jsonFilePath).readText()
            val players = Json.decodeFromString<List<Player>>(json)

            val csv = buildString {
                appendLine("name,allyCode,idName,stars,gear,omic,zeta")
                players.forEach { player ->
                    val char: Character? = player.chars.find { c -> c.idName.equals(charName) }
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
                appendLine("name,allyCode,idName,stars,gear,omic,zeta")
                for (charName in team.charList)
                players.forEach { player ->
                    val char: Character = player.chars.find { c -> c.equals(charName) }!!
                    appendLine("${player.name},${player.allyCode},${char.idName},${char.stars},${char.gear},${char.omic},${char.zeta}")

                }
            }

            return csv
        }

    }
}