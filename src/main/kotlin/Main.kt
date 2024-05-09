package com.tsepesh.thoma

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

suspend fun main() {
    val sheetsService = GoogleSheets()
    var range: String
    var addSheet = false
    val spreadsheetId = "11mh87CxIl6NqAcqAtV_5IwQ-3ja4iDn5Gq8rwIv7eEU"
    val filePath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\PlayerList.json"

    while (true) {
        println("1. Добавить таблицу \n" +
                "2. Собрать базу")
        when (readln().toInt()) {
            1 -> {
                println(
                    "1. Добавить в существующую таблицу\n" +
                            "2. Добавить в новую таблицу\n" +
                            "3. Закрыть"
                )
                when (readlnOrNull()?.toInt()) {
                    1 -> {
                        println("   Введите название таблицы")
                        range = readln()
                    }

                    2 -> {
                        println("   Введите название таблицы")
                        range = readln()
                        addSheet = true
                    }

                    3 -> {
                        break
                    }

                    else -> {
                        println("Invalid option")
                        continue
                    }
                }

                println(
                    "\n\nВыберите функцию\n" +
                            "   1. Добавить отряд\n" +
                            "   2. Добавить одного чара\n"
                )

                when (readln().toInt()) {
                    1 -> {
                        val team = Team()
                        for (i in 1..5) {
                            println("   Добавьте персонажей\n 1.Добавить \n 2.Выход")
                            val input = readln().toInt()
                            when (input) {
                                1 -> {
                                    println("Введите имя чара")
                                    team.addChar(readln())
                                    println("   Добавлен: ${team.charList[i - 1]}\n")
                                }

                                2 -> {
                                    break
                                }

                                else -> {
                                    println("неверный параметр, попробуйте снова")
                                }
                            }
                        }
                        val data = ParserHelper.parseCsvFile(JsonToCsvConverter.convertTeam(filePath, team))
                        if (data.isNotEmpty()) {
                            if (addSheet) {
                                sheetsService.createSheet(spreadsheetId, range)
                            }
                            sheetsService.appendData(spreadsheetId, range, data)

                            val sheetId = sheetsService.getSheetId(spreadsheetId, range)
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
                                sheetsService.appendData(spreadsheetId, "${range}!${rangeList[i]}1", team.charList[i])
                            }
                        } else {
                            println("No data found.")
                        }
                    }

                    2 -> {
                        println("   Введите имя чара")
                        val charName = readln()
                        val data = ParserHelper.parseCsvFile(JsonToCsvConverter.convertChar(filePath, charName))
                        if (data.isNotEmpty()) {
                            if (addSheet) {
                                sheetsService.createSheet(spreadsheetId, range)
                            }
                            sheetsService.appendData(spreadsheetId, range, data)
                        } else {
                            println("No data found.")
                        }
                    }
                }
            }
            2 -> {
                val file = Json.encodeToString(SwgohggParser.getPlayers(849418263u))
                File("AllCharList.json").writeText(file)
            }
        }
    }
}