package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.bot.BotDataHelper

class AddTeamToSheet: Extension() {
    override val name = "AddTeamToSheet"

    override suspend fun setup() {
        publicSlashCommand(::AddTeamToSheetArguments) {
            name = "addTeamToSheet"
            description = "Adds a team to sheet"
            action {
                BotDataHelper.addTeamToSheet(arguments.sheetName, arguments.isNewSheet.toBoolean(), arguments.teamName)
            }
        }
    }
    inner class AddTeamToSheetArguments : Arguments() {
        val sheetName by string {
            name = "sheet-name"
            description = "The sheet to add"
        }
        val isNewSheet by string {
            name = "is-new-sheet"
            description = "True - If you want to add this char to new team sheet"
        }
        val teamName by string {
            name = "team-name"
            description = "Team name"
        }
    }
}