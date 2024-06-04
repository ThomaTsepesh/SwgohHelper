package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.bot.BotDataHelper

class ParseData: Extension() {
    override val name = "parseData"

    override suspend fun setup() {
        publicSlashCommand(::DataArguments){
            name = "parseData"
            description = "Parse data."
            action {
                respond {
                    content = "data received. ${BotDataHelper.parseData(arguments.allycode)}"
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