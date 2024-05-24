package com.tsepesh.thoma

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.system.measureTimeMillis

val dataPath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\data\\"

class BotDataHelper {

    companion object {

        private const val SPREADSHEETID = "11mh87CxIl6NqAcqAtV_5IwQ-3ja4iDn5Gq8rwIv7eEU"
        private val sheetsService = GoogleSheets()
        private val FILEPATH: String = if (Files.exists(Paths.get(getLatestDate()))) {
            "${dataPath}getLatestDate()"
        } else {
            "${dataPath}GuildPlayers2024-05-23.json"
        }

        private val players = if(Files.exists(Paths.get(FILEPATH))){
            Json.decodeFromString<List<Player>>(File(FILEPATH).readText())
        }else{
            emptyList()
        }
        fun addCharToSheet(sheetName: String, charName: String, isNewSheet: Boolean) {
            if (isNewSheet){
                sheetsService.createSheet(SPREADSHEETID, sheetName)
            }
            val data = ParserHelper.parseCsvFile(ConverterJsonToCsv.convertChar(FILEPATH, charName))
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

            val data = ParserHelper.parseCsvFile(ConverterJsonToCsv.convertTeam(FILEPATH, team))
            if (data.isNotEmpty()) {

                sheetsService.replaceData(SPREADSHEETID, sheetName, data)

                val sheetId = sheetsService.getSheetId(SPREADSHEETID, sheetName)
                if (sheetId != null) {
                    var startColumn = 3
                    var endColumn = 8
                    for (i in 1..team.charList.size) {
                        sheetsService.mergeCells(SPREADSHEETID, sheetId, 0, 1, startColumn, endColumn)
                        startColumn += 5
                        endColumn += 5
                    }
                }
                val rangeList = listOf("D", "I", "N", "S", "X")
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!A1", "${LocalDate.now()}")
                for (i in 0 until team.charList.size) {
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${rangeList[i]}1", team.charList[i])
                }
            } else {
                println("No data found.")
            }
        }

        fun getCharacter(allyCode: Int, charName: String): Character? {
            if (players.isEmpty()){
                return Character("", 0, 0, "", 0, 0)
            }
            val player = players.firstOrNull{ it.allyCode == allyCode}
            return player?.getChar(charName)
        }

        fun updateCharacterData(sheetName: String, playerName: String, characterData: Character, charPosition: Int) {

            val range = "A2:AA52"
            val response = sheetsService.service.spreadsheets().values().get(SPREADSHEETID, "$sheetName!$range").execute()
            val values = response.getValues()
            val playerRow = values.indexOfFirst { it[0] == playerName }
            val coord1 = when(charPosition){ 1 -> "C"; 2 -> "H"; 3 -> "M"; 4 -> "R"; else -> "W"}
            val coord2 = when(charPosition){ 1 -> "G"; 2 -> "L"; 3 -> "Q"; 4 -> "V"; else -> "AA"}

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

        suspend fun parseData(allyCode: Int){
            val elapsedTime = measureTimeMillis {
                val file = Json.encodeToString(CrawlerSWgohGG.getPlayers(allyCode.toUInt()))
                File("${dataPath}\\GuildPlayers${LocalDate.now()}.json").writeText(file)
                println("База GuildPlayers${LocalDate.now()} сохранена ")
            }
            println("Время выполнения: ${elapsedTime / 1000.0} секунд")
        }

        fun getLatestDate(): String {
            val directory = File(dataPath)

            if (!directory.exists() || !directory.isDirectory) {

                return "Directory does not exist."
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val latestFile = directory.listFiles()
                ?.filter { it.isFile && it.name.endsWith(".json") && it.name.startsWith("GuildPlayers") }
                ?.maxByOrNull {
                    val datePart = it.name.substring(12, 22)
                    LocalDate.parse(datePart, formatter)
                }

            return if (latestFile != null) {
                println(latestFile)
                latestFile.name
            } else {
                "No files found in the directory."
            }
        }

    }
}