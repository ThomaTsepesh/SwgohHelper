package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.bot.BotDataHelper

class GetTeam: Extension() {
    override val name = "getTeamList"

    override suspend fun setup() {
        publicSlashCommand(::GetTeamArguments){
            name = "getteam"
            description = "Get team"
            action {
                respond { 
                    content = BotDataHelper.getTeam(arguments.teamName)
                }
            }
        }
    }
    inner class GetTeamArguments : Arguments() {
        val teamName by string {
            name = "teamname"
            description = "The name of the team"
        }
    }
}