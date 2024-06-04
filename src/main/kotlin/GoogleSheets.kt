package com.tsepesh.thoma

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import com.google.api.services.sheets.v4.model.Color
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

    fun createSheet(spreadsheetId: String, sheetTitle: String) {
        val addSheetRequest = AddSheetRequest().setProperties(
            SheetProperties().setTitle(sheetTitle)
        )
        if (sheetExists(spreadsheetId, sheetTitle)){
            println("a sheet with this name exists")
            return
        }
        val request = Request().setAddSheet(addSheetRequest)
        val updateRequest = BatchUpdateSpreadsheetRequest().setRequests(listOf(request))

        service.spreadsheets().batchUpdate(spreadsheetId, updateRequest).execute()

        println("Sheet created")
    }

    fun appendData(spreadsheetId: String, range: String, values: List<List<Any>>) {
        val valueRange = ValueRange().setValues(values)
        val appendRequest = service.spreadsheets().values().append(spreadsheetId, range, valueRange)
        appendRequest.valueInputOption = "USER_ENTERED"
        appendRequest.execute()

        println("Data updated")
    }

    fun appendData(spreadsheetId: String, range: String, text: String) {
        val values = listOf(listOf(text))
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

    fun getSheetId(spreadsheetId: String, sheetName: String): Int? {
        val response = service.spreadsheets().get(spreadsheetId).execute()
        val sheet = response.sheets?.find { it.properties?.title == sheetName }
        return sheet?.properties?.sheetId
    }

    fun mergeCells(spreadsheetId: String, sheetId: Int, startRowIndex: Int, endRowIndex: Int, startColumnIndex: Int, endColumnIndex: Int) {
        val mergeCellsRequest = MergeCellsRequest()
        mergeCellsRequest.range = GridRange()
        mergeCellsRequest.range.sheetId = sheetId
        mergeCellsRequest.range.startRowIndex = startRowIndex
        mergeCellsRequest.range.endRowIndex = endRowIndex
        mergeCellsRequest.range.startColumnIndex = startColumnIndex
        mergeCellsRequest.range.endColumnIndex = endColumnIndex
        mergeCellsRequest.mergeType = "MERGE_ALL"

        val request = Request().setMergeCells(mergeCellsRequest)
        val batchUpdateRequest = BatchUpdateSpreadsheetRequest().setRequests(listOf(request))

        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute()
    }

    fun overwriteData(spreadsheetId: String, range: String, values: List<List<Any>>) {
        val valueRange = ValueRange().setValues(values)
        val updateRequest = service.spreadsheets().values().update(spreadsheetId, range, valueRange)
        updateRequest.valueInputOption = "USER_ENTERED"
        updateRequest.execute()

        println("Data overwritten")
    }

    private fun clearSheet(spreadsheetId: String, range: String) {
        val clearRequest = service.spreadsheets().values().clear(spreadsheetId, range, ClearValuesRequest())
        clearRequest.execute()

        println("Sheet cleared")
    }

    fun replaceData(spreadsheetId: String, range: String, values: List<List<Any>>) {
        clearSheet(spreadsheetId, range)
        appendData(spreadsheetId, range, values)

        println("Data replaced")
    }

    fun sheetExists(spreadsheetId: String, sheetName: String): Boolean {
        return getSheetId(spreadsheetId, sheetName) != null
    }

    fun readData(spreadsheetId: String, range: String): MutableList<List<Any>> {
        val response = service.spreadsheets().values().get(spreadsheetId, "$range!A1:AB60").execute()
        return response.getValues()
    }

    fun colorCell(spreadsheetId: String, sheetId: Int, startRowIndex: Int, endRowIndex: Int, startColumnIndex: Int, endColumnIndex: Int, color: Color?) {
        val requests = ArrayList<Request>()
        val cellFormat = CellFormat().setBackgroundColor(color)
        val rowData = RowData().setValues(listOf(CellData().setUserEnteredFormat(cellFormat)))
        val gridRange = GridRange()
            .setSheetId(sheetId)
            .setStartRowIndex(startRowIndex)
            .setEndRowIndex(endRowIndex)
            .setStartColumnIndex(startColumnIndex)
            .setEndColumnIndex(endColumnIndex)

        val updateCellsRequest = UpdateCellsRequest()
            .setRange(gridRange)
            .setRows(listOf(rowData))
            .setFields("userEnteredFormat.backgroundColor")

        requests.add(Request().setUpdateCells(updateCellsRequest))

        val batchUpdateRequest = BatchUpdateSpreadsheetRequest().setRequests(requests)
        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute()

        println("Cell color updated")
    }
}

