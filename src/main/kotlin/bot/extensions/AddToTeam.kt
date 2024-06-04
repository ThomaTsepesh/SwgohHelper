package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.bot.BotDataHelper
import com.kotlindiscord.kord.extensions.commands.converters.impl.string

class AddToTeam: Extension() {
    override val name = "Add Team"

    override suspend fun setup() {
        publicSlashCommand(::AddToTeamArgs) {
            name = "addtoteam"
            description = "Adds a team to the bot"
            action {
                BotDataHelper.addAndSaveTeam(arguments.teamName, arguments.characters)
                respond {
                    content = "Added team"
                }
            }
        }
    }
    inner class AddToTeamArgs : Arguments() {
        val teamName by string {
            name = "team-name"
            description = "The name of the team to add"
        }
        val characters by string {
            name = "characters"
            description = "The characters of the team to add(separated by commas without spaces)"
        }
    }
}