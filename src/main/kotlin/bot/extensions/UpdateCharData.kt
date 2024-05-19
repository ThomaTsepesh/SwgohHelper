package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.tsepesh.thoma.BotDataHelper

class UpdateCharData: Extension() {
    override val name = "updateCharData"

    override suspend fun setup() {
        publicSlashCommand(::UpdateCharDataArguments){
            name = "updateChar"
            description = "Update char data"
            action {
                val char = BotDataHelper.getCharacter(arguments.allyCode, arguments.charName)
                if (char != null) {
                    BotDataHelper.updateCharacterData(arguments.sheetName, arguments.playerName, char, arguments.charPosition)
                }
                respond {
                    content = "Data updated"
                }
            }
        }
    }

    inner class UpdateCharDataArguments : Arguments() {
        val sheetName by string {
            name = "sheetname"
            description = "Sheet name"
        }
        val playerName by string {
            name = "playername"
            description = "Player name"
        }
        val charPosition by int {
            name = "charposition"
            description = "character position in the table"
        }
        val allyCode by int {
            name = "allycode"
            description = "Ally code"
        }
        val charName by string {
            name = "charname"
            description = "Character name"
        }

    }

}