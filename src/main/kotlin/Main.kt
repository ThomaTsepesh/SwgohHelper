package com.tsepesh.thoma

import kotlinx.coroutines.*

fun main() = runBlocking {
    val sheetsService = GoogleSheets()
    var range = ""
    var addSheet = false
    val spreadsheetId = "11mh87CxIl6NqAcqAtV_5IwQ-3ja4iDn5Gq8rwIv7eEU"
    val filePath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\PlayerList.json"

    while (true) {
        println("1. Добавить в существующую таблицу\n" +
                "2. Добавить в новую таблицу\n" +
                "3. Закрыть")
        when (readLine()?.toInt()) {
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

        println("\n\nВыберите функцию\n" +
        "   1. Добавить отряд\n" +
        "   2. Добавить одного чара\n")

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
}


