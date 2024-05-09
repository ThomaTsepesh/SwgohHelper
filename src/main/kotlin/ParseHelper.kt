package com.tsepesh.thoma

object ParserHelper {


    fun parseCsvFile(str: String): List<List<String>> {
        val result = mutableListOf<List<String>>()

        try {
            val lines = str.split("\n")
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
