package com.tsepesh.thoma

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import kotlin.system.measureTimeMillis

suspend fun main() {
    val sheetsService = GoogleSheets()
    var range: String
    var addSheet = false
    val spreadsheetId = "11mh87CxIl6NqAcqAtV_5IwQ-3ja4iDn5Gq8rwIv7eEU"
        //"1Jo87ybi9OLDPqa3nqV44hukZZcQLQqy6hxez13kV72E"
        // сопы "1d32Vdx92x65MUQ4dpeT-Q6lY5xJFitt8eyk5H3XcWo8" // наши
    var filePath = "Не задана"
    val dataPath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\data"
    val allyCode = 849418263u

    //849418263

    //10342

    // не сохраняет базу, пока программа запущена
    // поменять все пути файлов под себя. для меня пока и такие нормально


    println("\nВведите дату нужной базы(пример даты: 2024-05-03)\n")

    val input = readln()
    if (Files.exists(Paths.get("${dataPath}\\GuildPlayers${input}.json"))) {
        filePath = "${dataPath}\\GuildPlayers${input}.json"
        println("Выбрана $filePath")
    } else {
        println("Файл не существует")
    }

    while (true) {
        println("Текущая база: $filePath")

        println(
            "\n1. Добавить в таблицу \n" +
                    "2. Собрать базу\n" +
                    "3. Создать отряд\n" +
                    "4. выбрать другую базу"
        )
        when (readln()) {
            "1" -> {
                println(
                    "1. Добавить в существующую таблицу\n" +
                            "2. Добавить в новую таблицу\n" +
                            "3. Закрыть"
                )
                when (readln()) {
                    "1" -> {
                        println("   Введите название таблицы")
                        range = readln()
                    }

                    "2" -> {
                        println("   Введите название таблицы")
                        range = readln()
                        addSheet = true
                    }

                    "3" -> {
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

                when (readln()) {
                    "1" -> {
//                        val team = Team()
                        println("Введите персонажей через запятую, без пробелов.э\n")
                        val chars = readln()
                        val team = Team()
                        chars.split(",").forEach{str -> team.addChar(str)}
//                        for (i in 1..5) {
//                            println("   Добавьте персонажей\n 1.Добавить \n 2.Выход")
//                            val input = readln().toInt()
//                            when (input) {
//                                1 -> {
//                                    println("Введите имя чара")
//                                    team.addChar(readln())
//                                    println("   Добавлен: ${team.charList[i - 1]}\n")
//                                }
//
//                                2 -> {
//                                    break
//                                }
//                            }
//                        }
                        val data = ParserHelper.parseCsvFile(ConverterJsonToCsv.convertTeam(filePath, team))
                        if (data.isNotEmpty()) {
                            if (addSheet) {
                                sheetsService.createSheet(spreadsheetId, range)
                            }
                            sheetsService.appendData(spreadsheetId, range, data)

                            val sheetId = sheetsService.getSheetId(spreadsheetId, range)
                            if (sheetId != null) {
                                var startColumn = 2
                                var endColumn = 7
                                for (i in 1..team.charList.size) {
                                    sheetsService.mergeCells(spreadsheetId, sheetId, 0, 1, startColumn, endColumn)
                                    startColumn += 5
                                    endColumn += 5
                                }
                            }
                            val rangeList = listOf("C", "H", "M", "R", "W")
                            for (i in 0 until team.charList.size) {
                                sheetsService.appendData(spreadsheetId, "${range}!${rangeList[i]}1", team.charList[i])
                            }
                        } else {
                            println("No data found.")
                        }
                    }

                    "2" -> {
                        println("   Введите имя чара")
                        val charName = readln()
                        val data = ParserHelper.parseCsvFile(ConverterJsonToCsv.convertChar(filePath, charName))
                        if (data.isNotEmpty()) {
                            if (addSheet) {
                                sheetsService.createSheet(spreadsheetId, range)
                            }
                            sheetsService.appendData(spreadsheetId, range, data)
                        } else {
                            println("No data found.")
                        }
                    }

                    else -> {
                        println("Invalid option")
                    }
                }
            }

            "2" -> {
                val elapsedTime = measureTimeMillis {
                    val file = Json.encodeToString(CrawlerSWgohGG.getPlayers(allyCode))
                    File("${dataPath}\\GuildPlayers${LocalDate.now()}.json").writeText(file)
                    println("База GuildPlayers${LocalDate.now()} сохранена ")
                }
                println("Время выполнения: ${elapsedTime / 1000.0} секунд")

            }

            "3" -> {
                while (true) {
                    println(
                        "1. Добавить отряд\n" +
                                "2. Назад"
                    )
                    val team = Team()
                    when (readln()) {

                        "1" -> {
                            println("\n[")

                            while (team.charList.size < 5) {
                                println(
                                    "1. Добавить персонажа\n" +
                                            "2. Подтвердить"
                                )
                                val input = readln().toInt()
                                when (input) {
                                    1 -> {
                                        println("Введите имя чара")
                                        val string = readln()
                                        team.addChar(string)
                                        println("   Добавлен: ${string}\n")
                                    }

                                    2 -> {
                                        break
                                    }
                                }
                            }
                            println("\n]")
                        }

                        "2" -> {
                            break
                        }

                        else -> {
                            println("Invalid option")
                            continue
                        }
                    }
                }
            }

            "4" -> {
                println("Введите дату нужной базы(пример даты: 2024-05-03)\n")
                val input = readln()
                if (Files.exists(Paths.get("${dataPath}\\GuildPlayers${input}.json"))) {
                    filePath = "${dataPath}\\GuildPlayers${input}.json"
                    println("Выбрана $filePath")
                } else {
                    println("Файл не существует")
                }
            }

            else -> {
                println("Invalid option")
            }
        }
    }
}
