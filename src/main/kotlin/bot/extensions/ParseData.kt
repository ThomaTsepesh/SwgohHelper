package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.BotDataHelper

class ParseData: Extension() {
    override val name = "parseData"

    override suspend fun setup() {
        publicSlashCommand(::DataArguments){
            name = "parseData"
            description = "Parse data."
            action {
                BotDataHelper.parseData(arguments.allycode)
                respond {
                    content = "data received"
                }
            }
        }
    }
    inner class DataArguments : Arguments() {
        val allycode by int {
            name = "allycode"
            description = "allycode"
        }
    }
}