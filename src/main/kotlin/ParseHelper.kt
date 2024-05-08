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

    fun parseCsvFile(str: String): List<List<String>> {
        val result = mutableListOf<List<String>>()

        try {
            var lines = str.split("\n")
            for (line in lines) {
                if (line.isBlank()) {
                    continue
                }

                val values = line.split(",")
                result.add(values)
            }
        } catch (e: Exception) {
            println("error parsing the CSV file: ${e.message}")
            throw e
        }

        return result
    }

}
