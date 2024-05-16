package com.tsepesh.thoma

import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

class DataHelper {

    companion object {
        private const val SPREADSHEETID = "11mh87CxIl6NqAcqAtV_5IwQ-3ja4iDn5Gq8rwIv7eEU"
        private val sheetsService = GoogleSheets()
        private const val DATAPATH = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\data\\GuildPlayers2024-05-16.json"
        private val players = Json.decodeFromString<List<Player>>(File(DATAPATH).readText())

        fun addCharToSheet(sheetName: String, charName: String, isNewSheet: Boolean) {
            if (isNewSheet){
                sheetsService.createSheet(SPREADSHEETID, sheetName)
            }
            val data = ParserHelper.parseCsvFile(ConverterJsonToCsv.convertChar(DATAPATH, charName))
            if (data.isNotEmpty()) {
                sheetsService.replaceData(SPREADSHEETID, sheetName, data)
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!I1", "${LocalDate.now()}")
            }
        }

        fun addTeamToSheet(sheetName: String, chars: String, isNewSheet:Boolean){
            if (isNewSheet){
                sheetsService.createSheet(SPREADSHEETID, sheetName)
            }
            val team = Team()
            chars.split(",").forEach{str -> team.addChar(str)}

            val data = ParserHelper.parseCsvFile(ConverterJsonToCsv.convertTeam(DATAPATH, team))
            if (data.isNotEmpty()) {

                sheetsService.replaceData(SPREADSHEETID, sheetName, data)

                val sheetId = sheetsService.getSheetId(SPREADSHEETID, sheetName)
                if (sheetId != null) {
                    var startColumn = 2
                    var endColumn = 6
                    for (i in 1..team.charList.size) {
                        sheetsService.mergeCells(SPREADSHEETID, sheetId, 0, 1, startColumn, endColumn)
                        startColumn += 4
                        endColumn += 4
                    }
                }
                val rangeList = listOf("C", "G", "K", "O", "S")
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!A1", "${LocalDate.now()}")
                for (i in 0 until team.charList.size) {
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${rangeList[i]}1", team.charList[i])
                }
            } else {
                println("No data found.")
            }
        }

        fun getCharacter(allyCode: Int, charName: String): Character? {
            val player = players.firstOrNull{ it.allyCode == allyCode}
            return player?.getChar(charName)
        }

        fun updateCharacterData(sheetName: String, playerName: String, characterData: Character, charPosition: Int) {

            val range = "A2:V52"
            val response = sheetsService.service.spreadsheets().values().get(SPREADSHEETID, "$sheetName!$range").execute()
            val values = response.getValues()
            val playerRow = values.indexOfFirst { it[0] == playerName }
            val coord1 = when(charPosition){ 1 -> "C"; 2 -> "G"; 3 -> "K"; 4 -> "O"; else -> "S"}
            val coord2 = when(charPosition){ 1 -> "F"; 2 -> "J"; 3 -> "N"; 4 -> "R"; else -> "V"}

            if (playerRow != -1) {
                val newValues = listOf(
                    characterData.stars.toString(),
                    characterData.gear,
                    characterData.omic.toString(),
                    characterData.zeta.toString()
                )
                sheetsService.overwriteData(SPREADSHEETID, "$sheetName!${coord1}${playerRow + 2}:${coord2}${playerRow + 2}", listOf(newValues))

            } else {
                println("Player not found")
            }
        }

    }
}