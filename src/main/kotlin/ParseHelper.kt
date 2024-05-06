package com.tsepesh.thoma

import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

object ParserHelper {
    fun getCsvFiles(dirPath: String): List<String> {
        val directory = File(dirPath)
        val files = directory.listFiles { _, name -> name.endsWith(".csv") }
        return files?.map { it.path } ?: emptyList()
    }

    fun parseCsvFile(path: String): List<List<String>> {
        val result = mutableListOf<List<String>>()

        try {
            val reader = BufferedReader(FileReader(path))
            var line: String? = reader.readLine()
            while (line != null) {
                if (line.isBlank()) {
                    continue
                }

                val values = line.split(",")
                result.add(values)
                line = reader.readLine()
            }
        } catch (e: IOException) {
            println("error parsing the CSV file: ${e.message}")
            throw e
        }

        return result
    }

}
