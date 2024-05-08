package com.tsepesh.thoma

import kotlinx.coroutines.*

fun main() = runBlocking {
    val sheetsService = GoogleSheets()
    val range = "Fulcrum"
    val spreadsheetId = "11mh87CxIl6NqAcqAtV_5IwQ-3ja4iDn5Gq8rwIv7eEU"
    val filePath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\data\\PlayerListSeria.json"
    while (true){
        println("\n\nВыберите функцию")
        println("1. Добавить тиму")
        println("2. Добавить одного чара")
        println("3. Выход")

        when(readln().toInt()){
            1 -> {
                val team = Team()
                for (i in 1 until 5) {
                    println("   Добавьте персонажей\n 1.Добавить \n 2. Выход")
                    val input = readln().toInt()
                    when (input) {
                        1 -> {
                            team.addChar(readln())
                        }
                        2 -> {
                            break
                        }
                        else -> {
                            println("неверный параметр, попробуйте снова")
                        }
                    }
                }


                val csv = JsonToCsvConverter.convertTeam(filePath, team)
            }
            2 -> {
                println("   Введите имя чара")
                val charName = readln()
                val data = ParserHelper.parseCsvFile(JsonToCsvConverter.convertChar(filePath, charName))
                if (data.isNotEmpty()) {
                    sheetsService.appendData(spreadsheetId, range, data)
                } else {
                    println("No data found in file.")
                }
            }
            4 ->{
               println(JsonToCsvConverter.convertChar(filePath, charName = "ahsoka-tano-fulcrum"))
            }
            3 -> {
                break
            }
            else -> continue
        }
    }
}


