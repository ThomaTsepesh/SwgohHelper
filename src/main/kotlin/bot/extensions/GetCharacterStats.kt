package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.tsepesh.thoma.CrawlerSWgohGG


class GetCharacterStats: Extension() {
    override val name = "getCharacterStats"

    override suspend fun setup() {
        publicSlashCommand(::AllyCodeArguments){
            name = "getStats"
            description = "Returns a character's stats"
            action{
                respond {
                    content = "${CrawlerSWgohGG.getCharStats((arguments.args1).toUInt(), arguments.args2)}"
                }
            }
        }
    }
    inner class AllyCodeArguments: Arguments(){
        val args1 by string {
            name = "allycode"
            description = "allyCode"
        }
        val args2 by string {
            name = "charactername"
            description = "Returns a character's stats"
        }
    }
}