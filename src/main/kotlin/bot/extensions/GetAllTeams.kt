package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.bot.BotDataHelper

class GetAllTeams: Extension() {
    override val name = "getAllTeams"

    override suspend fun setup() {
     publicSlashCommand() {
         name = "getAllTeams"
         description = "Get all teams"
         action {
             respond {
                 content = BotDataHelper.getAllTeams()
             }
         }
     }
    }
}