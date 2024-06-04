package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.bot.BotDataHelper

class AddPlayerToSheet: Extension() {
    override val name = "AddPlayerToSheet"

    override suspend fun setup() {
        publicSlashCommand(::AddPlayerToSheetArguments){
            name = "AddPlayerToSheet"
            description = "Adds a player to sheet"
            action {
                respond {
                    content = BotDataHelper.addPlayersSheet(arguments.sheetName)
                }
            }
        }
    }
    inner class AddPlayerToSheetArguments : Arguments() {
        val sheetName by string {
            name = "sheetname"
            description = "The name of the sheet"
        }
    }
}