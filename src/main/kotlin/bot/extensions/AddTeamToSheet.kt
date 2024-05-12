package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.DataHelper

class AddTeamToSheet: Extension() {
    override val name: String = "Add New Sheet Team"
    override suspend fun setup() {
        publicSlashCommand(::AddNewTeamSheetArguments){
            name = "addTeamToSheet"
            description = "add data"
            action {
                DataHelper.addTeamToSheet(arguments.sheetName, arguments.charsName, arguments.isNewSheet.toBoolean())
                respond {
                    content = "data has been added to the table"
                }
            }
        }
    }
    inner class AddNewTeamSheetArguments : Arguments() {
        val sheetName by string {
            name = "sheetname"
            description = "The sheet to add"
        }
        val isNewSheet by string {
            name = "isnewsheet"
            description = "True - If you want to add this char to new team sheet"
        }
        val charsName by string {
            name = "charsname"
            description = "Characters name(char1,char2...)"
        }
    }
}