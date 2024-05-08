package com.tsepesh.thoma

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import java.io.FileInputStream

class GoogleSheets {
    private val applicationName = "Google Sheets API Kotlin Quickstart"
    private val jsonPath = "C:\\Users\\1\\IdeaProjects\\SwgohHelper\\src\\main\\kotlin\\SwgohHelperToken.json"
    private val scopes = listOf(SheetsScopes.SPREADSHEETS)
    private lateinit var service: Sheets

    init {
        initializeService()
    }

    private fun initializeService() {
        val credential = GoogleCredential
            .fromStream(FileInputStream(jsonPath))
            .createScoped(scopes)

        service = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(applicationName)
            .build()

        println("Google initialized successfully")
    }

    fun appendData(spreadsheetId: String, range: String, values: List<List<Any>>) {
        val valueRange = ValueRange().setValues(values)
        val appendRequest = service.spreadsheets().values().append(spreadsheetId, range, valueRange)
        appendRequest.valueInputOption = "USER_ENTERED"
        appendRequest.execute()

        println("Data updated")
    }
    companion object {
        private val HTTP_TRANSPORT = NetHttpTransport()
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    }
}
