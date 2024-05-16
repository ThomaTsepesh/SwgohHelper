package com.tsepesh.thoma.bot
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull
import com.tsepesh.thoma.bot.extensions.*

private val TOKEN = env("TOKEN")

class SWHelperBot {
}

suspend fun main() {
    val bot = ExtensibleBot(TOKEN){
        applicationCommands {
            defaultGuild(envOrNull("swgohbots"))
        }
        extensions {
            add(::AbobaExtension)
            add(::GetCharacterStats)
            add(::AddCharToSheet)
            add(::AddTeamToSheet)
            add(::UpdateCharData)
        }
    }
    bot.start()
}
