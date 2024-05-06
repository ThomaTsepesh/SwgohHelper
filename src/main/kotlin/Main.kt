package com.tsepesh.thoma

import kotlinx.coroutines.*
import java.io.File
import java.nio.file.Paths

fun main() = runBlocking {
    val sheetsService = GoogleSheets()
    val range = ""
    val spreadsheetId = ""

    val files = File(Paths.get("").toAbsolutePath().toString()).walk().filter { it.extension == "csv" }.toList()
    for (file in files) {
        println("Processing file: ${file.name}")
        val data = ParserHelper.parseCsvFile(file.toString())
        if (data.isNotEmpty()) {
            sheetsService.appendData(spreadsheetId, range, data)
        } else {
            println("No data found in file.")
        }
    }
}


