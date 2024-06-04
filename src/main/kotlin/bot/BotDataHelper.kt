package com.tsepesh.thoma.bot

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.tsepesh.thoma.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.system.measureTimeMillis
import com.google.api.services.sheets.v4.model.Color
import kotlinx.coroutines.delay


const val dataPath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\data\\"
private const val maxAttempts = 5

class BotDataHelper {

    companion object {

        private const val SPREADSHEETID = /*"11mh87CxIl6NqAcqAtV_5IwQ-3ja4iDn5Gq8rwIv7eEU" */
            "1Jo87ybi9OLDPqa3nqV44hukZZcQLQqy6hxez13kV72E"
        private const val TEAMPATH = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\bot"
        private val sheetsService = GoogleSheets()
        private val FILEPATH: String = "$dataPath${getCurrentData()}"

//            if (Files.exists(getLatestDate())) {
//            getLatestDate()
//        } else {
//            "${dataPath}GuildPlayers2024-05-26.json"
//        }

        private val players = if (Files.exists(Paths.get(FILEPATH))) {
            Json.decodeFromString<List<Player>>(File(FILEPATH).readText())
        } else {
            emptyList()
        }

        private val teams = if (Files.exists(Paths.get("$TEAMPATH\\Teams.json"))) {
            Json.decodeFromString<HashMap<String, String>>(File("$TEAMPATH\\Teams.json").readText())
        } else {
            HashMap()
        }

        fun addAndSaveTeam(teamName: String, characters: String) {
            if (!teams.contains(teamName)) {
                teams[teamName] = characters
                val file = Json.encodeToString(teams)
                File("$TEAMPATH\\Teams.json").writeText(file)
            }
        }

        fun getTeam(teamName: String): String {
            if (teams.contains(teamName)) {
                return teams[teamName].toString()
            }
            return "there is no such team"
        }

        fun getAllTeams(): String {
            if (teams.isNotEmpty()) {
                val team = teams.map { it.key }.joinToString(",")
                return team
            }
            return "List Empty"
        }

        fun addCharToSheet(sheetName: String, charName: String, isNewSheet: Boolean) {
            if (isNewSheet) {
                sheetsService.createSheet(SPREADSHEETID, sheetName)
            }
            val data = JsonHelper.parseChar(FILEPATH, charName)
            if (data.isNotEmpty()) {
                sheetsService.replaceData(SPREADSHEETID, sheetName, data)
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!A1", "${LocalDate.now()}")
            }
            val sheetId = sheetsService.getSheetId(SPREADSHEETID, sheetName)
            if (sheetId != null) {
                val startColumn = 2
                val endColumn = 7
                sheetsService.mergeCells(SPREADSHEETID, sheetId, 0, 1, startColumn, endColumn)
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!C1", charName)
            }
        }

        fun addCharsToSheet(sheetName: String, chars: String, isNewSheet: Boolean) {
            if (isNewSheet) {
                sheetsService.createSheet(SPREADSHEETID, sheetName)
            }
            val team = Team.toTeam(chars)

            val data = JsonHelper.parseTeam(FILEPATH, team)
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

        fun addTeamToSheet(sheetName: String, isNewSheet: Boolean, teamName: String) {
            println(FILEPATH)
            if (isNewSheet) {
                sheetsService.createSheet(SPREADSHEETID, sheetName)
            }
            val team = teams[teamName].toString()
            val charList = teams[teamName].toString().split(",")
            val data = JsonHelper.parseTeam(FILEPATH, Team.toTeam(team))
            if (data.isNotEmpty()) {

                sheetsService.replaceData(SPREADSHEETID, sheetName, data)

                val sheetId = sheetsService.getSheetId(SPREADSHEETID, sheetName)
                if (sheetId != null) {
                    var startColumn = 3
                    var endColumn = 8
                    for (i in 1..charList.size) {
                        sheetsService.mergeCells(SPREADSHEETID, sheetId, 0, 1, startColumn, endColumn)
                        startColumn += 5
                        endColumn += 5
                    }
                }
                val rangeList = listOf("D", "I", "N", "S", "X")
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!A1", "${LocalDate.now()}")
                for (i in charList.indices) {
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${rangeList[i]}1", charList[i])
                }

                sheetsService.appendData(SPREADSHEETID, "${sheetName}!A2", "name")
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!B2", "allyCode")
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!C2", "totalGP")
                var counter = 0
                if (charList.size > counter){
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+3)}", "stars")
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+4)}", "charGP")
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+5)}", "gear")
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+6)}", "omic")
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+7)}", "zeta")
                    counter+=5
                }

            } else {
                println("No data found.")
            }
        }
        fun addDataToSheet(sheetName: String, data: List<List<String>>, team: Team) {
            println(FILEPATH)
            if (data.isNotEmpty()) {
                sheetsService.replaceData(SPREADSHEETID, sheetName, data)
                val charList = team.charList
                val sheetId = sheetsService.getSheetId(SPREADSHEETID, sheetName)
                if (sheetId != null) {
                    var startColumn = 3
                    var endColumn = 8
                    for (i in 1..charList.size) {
                        sheetsService.mergeCells(SPREADSHEETID, sheetId, 0, 1, startColumn, endColumn)
                        startColumn += 5
                        endColumn += 5
                    }
                }
                val rangeList = listOf("D", "I", "N", "S", "X")
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!A1", "${LocalDate.now()}")
                for (i in 0 until charList.size) {
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${rangeList[i]}1", charList[i])
                }

                sheetsService.appendData(SPREADSHEETID, "${sheetName}!A2", "name")
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!B2", "allyCode")
                sheetsService.appendData(SPREADSHEETID, "${sheetName}!C2", "totalGP")
                var counter = 0
                if (charList.size > counter){
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+3)}", "stars")
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+4)}", "charGP")
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+5)}", "gear")
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+6)}", "omic")
                    sheetsService.appendData(SPREADSHEETID, "${sheetName}!${getCellCoordinates(1, counter+7)}", "zeta")
                    counter+=5
                }
            } else {
                println("No data found.")
            }
        }

        fun getCharacter(allyCode: Int, charName: String): Character? {
            if (players.isEmpty()) {
                return Character("", 0, 0, "", 0, 0)
            }
            val player = players.firstOrNull { it.allyCode == allyCode }
            return player?.getChar(charName)
        }

//        fun updateCharacterData(sheetName: String, playerName: String, characterData: Character, charPosition: Int) {
            /*
                        val range = "A2:AB52"
                        val response =
                            sheetsService.service.spreadsheets().values().get(SPREADSHEETID, "$sheetName!$range").execute()
                        val values = response.getValues()
                        val playerRow = values.indexOfFirst { it[0] == playerName }
                        val coord1 = when (charPosition) {
                            1 -> "C"; 2 -> "H"; 3 -> "M"; 4 -> "R"; else -> "W"
                        }
                        val coord2 = when (charPosition) {
                            1 -> "G"; 2 -> "L"; 3 -> "Q"; 4 -> "V"; else -> "AA"
                        }

                        if (playerRow != -1) {
                            val newValues = listOf(
                                characterData.stars,
                                characterData.gear,
                                characterData.omic.toString(),
                                characterData.zeta.toString()
                            )
                            sheetsService.overwriteData(
                                SPREADSHEETID,
                                "$sheetName!${coord1}${playerRow + 2}:${coord2}${playerRow + 2}",
                                listOf(newValues)
                            )

                        } else {
                            println("Player not found")
                        }
             */
//        }

        suspend fun parseData(allyCode: Int): String {
            val elapsedTime = measureTimeMillis {
                val file = Json.encodeToString(CrawlerSWgohGG.getPlayers(allyCode.toUInt()))
                File("$dataPath\\GuildPlayers${LocalDate.now()}.json").writeText(file)
                println("База GuildPlayers${LocalDate.now()} сохранена ")
            }
            println("Время выполнения: ${elapsedTime / 1000.0} секунд")
            return "Время выполнения: ${elapsedTime / 1000.0} секунд"
        }

        fun addPlayersSheet(sheetName: String): String {
            if (sheetsService.sheetExists(SPREADSHEETID, sheetName)) {
                return "a sheet with this name exists"
            }
            sheetsService.createSheet(SPREADSHEETID, sheetName)
            sheetsService.appendData(
                SPREADSHEETID,
                "$sheetName!A1:B53",
                JsonHelper.parsePlayers(FILEPATH)
            )
            return "data has been added"
        }

        private fun getCurrentData(): String {
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
                "No files found the directory."
            }
        }

        suspend fun paintChanges(teamName: String, newSheetName: String): String{
            val team = teams[teamName].toString()
            if (team.isEmpty()){
                return ""
            }
            sheetsService.createSheet(SPREADSHEETID, newSheetName)
            paintSheet(newSheetName, calculateDiff(Team.toTeam(team), newSheetName))
            return "Done"
        }

        private fun getLatestData(): String {
            val directory = File(dataPath)

            if (!directory.exists() || !directory.isDirectory) {
                return "Directory not exist"
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var daysAgo = 7

            val files = directory.listFiles()
                ?.filter { it.isFile && it.name.endsWith(".json") && it.name.startsWith("GuildPlayers") }
                ?.sortedByDescending {
                    val datePart = it.name.substring(12, 22)
                    LocalDate.parse(datePart, formatter)
                }

            while (daysAgo <= 14) {
                val targetDate = LocalDate.now().minusDays(daysAgo.toLong())

                val targetFile = files?.firstOrNull {
                    val datePart = it.name.substring(12, 22)
                    LocalDate.parse(datePart, formatter).isEqual(targetDate)
                }

                if (targetFile != null) {
                    return targetFile.name
                }
                daysAgo++
            }
            return "No files found"
        }

        private suspend fun paintSheet(sheetName: String, colorMap: HashMap<String, Color>) {
            var attempt = 0
            val id = sheetsService.getSheetId(SPREADSHEETID, sheetName)
            for (coord in colorMap.keys) {
                while (attempt < CrawlerSWgohGG.maxAttempts) {
                    try {
                        println(coord)
                        println(colorMap[coord])
                        val indices = getCellIndices(coord).split(",")
                        if (id != null) {
                            sheetsService.colorCell(
                                SPREADSHEETID,
                                id,
                                indices[0].toInt(),
                                indices[1].toInt(),
                                indices[2].toInt(),
                                indices[3].toInt(),
                                colorMap[coord]
                            )
                        }
                        break
                    } catch (e: GoogleJsonResponseException) {
                        attempt++
                        println("attempt= $attempt")
                        if (attempt < maxAttempts) {
                            delay(10000)
                        } else {
                            throw e
                        }
                    }
                }
            }
        }

        private fun calculateDiff(team: Team, newSheetName: String): HashMap<String, Color> {
            val result = HashMap<String, Color>()
            val latestDataDate = getLatestData()
            println("LatestDate = $latestDataDate")

            val latestData = JsonHelper.parseTeam("$dataPath$latestDataDate", team).toMutableList()
            val currentData = JsonHelper.parseTeam(FILEPATH, team).toMutableList()
            val lastNames = latestData.mapNotNull { it.firstOrNull() }.toSet()
            val currentNames = currentData.mapNotNull { it.firstOrNull() }.toSet()

            val missingPlayers = lastNames - currentNames
            val newPlayers = currentNames - lastNames

            currentData.removeIf{ p-> p.firstOrNull() in newPlayers}
            latestData.removeIf{ p-> p.firstOrNull() in missingPlayers}
            for (i in currentData.indices){
                if (currentData[i].size <= 3)
                        continue
                if(currentData[i].firstOrNull() == latestData[i].firstOrNull()){
                    for (j in 5 .. 7) {
                        if (currentData[i][j] != latestData[i][j]) {
                            val color = Color()
                            color.red = 1.0f
                            result[getCellCoordinates(i, j)] = color
                        }
                    }
                }
            }
            addDataToSheet(newSheetName, currentData, team)
            return result
        }

        private fun getColumnLetter(number: Int): String {
            var number = number
            val letter = StringBuilder()
            while (number > 0) {
                val mod = (number - 1) % 26
                letter.insert(0, ('A' + mod))
                number = (number - mod) / 26
            }
            return letter.toString()
        }

        fun getCellCoordinates(rowIndex: Int, columnIndex: Int): String {
            val columnLetter = getColumnLetter(columnIndex + 1)
            return "$columnLetter${rowIndex + 1}"
        }

        fun getCellIndices(cell: String): String {
            val columnPart = cell.filter { it.isLetter() }
            val rowPart = cell.filter { it.isDigit() }

            val startColumnIndex = columnPart.fold(0) { sum, current ->
                sum * 26 + (current.uppercaseChar().code - 'A'.code + 1)
            } - 1

            val startRowIndex = rowPart.toInt() - 1
            val endColumnIndex = startColumnIndex + 1
            val endRowIndex = startRowIndex + 1

            return "$startRowIndex,$endRowIndex,$startColumnIndex,$endColumnIndex"
        }


    }
}