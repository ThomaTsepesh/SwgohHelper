package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.bot.BotDataHelper

class PaintChanges: Extension() {
    override val name = "PaintChanges"

    override suspend fun setup() {
        publicSlashCommand(::PaintChangesArg){
            name = "PaintChanges"
            description = "Paint changes"
            action {
                respond {
                    content = "${BotDataHelper.paintChanges( arguments.teamName, arguments.sheetname)}"
                }
            }
        }
    }
    inner class PaintChangesArg : Arguments() {
        val sheetname by string {
            name = "sheet"
            description = "Sheet ID"
        }
        val teamName by string {
            name = "team-name"
            description = "team name"
        }

    }
}