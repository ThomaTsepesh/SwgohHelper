package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand

class AbobaExtension: Extension() {
    override val name = "aboba"

    override suspend fun setup() {
        publicSlashCommand(::AbobaArguments){
            name = "aboba"
            description = "кто абоба"
            action {
                respond {
                    content = "${arguments.target.mention} is aboba"
                }
            }
        }
    }
    inner class AbobaArguments: Arguments() {
        val target by user{
            name = "user"
            description = "aboba user"
        }

    }
}