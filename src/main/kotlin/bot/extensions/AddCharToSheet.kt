package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.DataHelper

class AddCharToSheet: Extension() {
    override val name = "AddCharToSheet"
    override suspend fun setup() {
        publicSlashCommand(::AddNewSheetArguments){
            name = "addCharToSheet"
            description = "add data"
            action {

                DataHelper.addCharToSheet(arguments.sheetName, arguments.charName, arguments.isNewSheet.toBoolean())
                respond {
                    content = "data has been added to the table"
                }
            }
        }
    }
    inner class AddNewSheetArguments : Arguments() {
        val sheetName by string {
            name = "sheetname"
            description = "The sheet to add"
        }
        val isNewSheet by string {
            name = "isnewsheet"
            description = "True - If you want to add this char to new sheet"
        }
        val charName by string {
            name = "charname"
            description = "Character name"
        }
    }
}