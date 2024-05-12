package com.tsepesh.thoma

class DataHelper {

    companion object {
        val spreadsheetId = "11mh87CxIl6NqAcqAtV_5IwQ-3ja4iDn5Gq8rwIv7eEU"
        val sheetsService = GoogleSheets()
        val dataPath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\data\\GuildPlayers2024-05-11.json"


        fun addCharToSheet(sheetName: String, charName: String, isNewSheet: Boolean) {
            if (isNewSheet){
                sheetsService.createSheet(spreadsheetId, sheetName)
            }
            val data = ParserHelper.parseCsvFile(ConverterJsonToCsv.convertChar(dataPath, charName))
            if (data.isNotEmpty()) {
                sheetsService.replaceData(spreadsheetId, sheetName, data)
            }
        }

        fun addTeamToSheet(sheetName: String, chars: String, isNewSheet:Boolean){
            if (isNewSheet){
                sheetsService.createSheet(spreadsheetId, sheetName)
            }
            val team = Team()
            chars.split(",").forEach{str -> team.addChar(str)}

            val data = ParserHelper.parseCsvFile(ConverterJsonToCsv.convertTeam(dataPath, team))
            if (data.isNotEmpty()) {

                sheetsService.replaceData(spreadsheetId, sheetName, data)

                val sheetId = sheetsService.getSheetId(spreadsheetId, sheetName)
                if (sheetId != null) {
                    var startColumn = 2
                    var endColumn = 6
                    for (i in 1..team.charList.size) {
                        sheetsService.mergeCells(spreadsheetId, sheetId, 0, 1, startColumn, endColumn)
                        startColumn += 4
                        endColumn += 4
                    }
                }
                val rangeList = listOf("C", "G", "K", "O", "S")
                for (i in 0 until team.charList.size) {
                    sheetsService.appendData(spreadsheetId, "${sheetName}!${rangeList[i]}1", team.charList[i])
                }
            } else {
                println("No data found.")
            }
        }
    }
}