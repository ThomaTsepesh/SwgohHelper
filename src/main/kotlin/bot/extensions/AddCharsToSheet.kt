package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.bot.BotDataHelper

class AddCharsToSheet: Extension() {
    override val name: String = "Add New Sheet Team"
    override suspend fun setup() {
        publicSlashCommand(::AddNewTeamSheetArguments){
            name = "addCharsToSheet"
            description = "add data"
            action {
                BotDataHelper.addCharsToSheet(arguments.sheetName, arguments.charsName, arguments.isNewSheet.toBoolean())
                respond {
                    content = "data has been added to the table"
                }
            }
        }
    }
    inner class AddNewTeamSheetArguments : Arguments() {
        val sheetName by string {
            name = "sheet-name"
            description = "The sheet to add"
        }
        val isNewSheet by string {
            name = "is-new-sheet"
            description = "True - If you want to add this char to new team sheet"
        }
        val charsName by string {
            name = "chars-name"
            description = "Characters name(char1,char2...)"
        }
    }
}