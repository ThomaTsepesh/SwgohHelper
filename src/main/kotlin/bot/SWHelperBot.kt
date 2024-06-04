package com.tsepesh.thoma.bot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.envOrNull
import com.tsepesh.thoma.bot.extensions.*
import io.ktor.client.*
import io.ktor.client.plugins.*

private val TOKEN = env("TOKEN")

class SWHelperBot

suspend fun main() {

    val httpClientKord = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 600000  // Установите время ожидания запроса в миллисекундах
        }
    }
    val bot = ExtensibleBot(TOKEN) {
        kord {
            httpClient = httpClientKord
        }
        applicationCommands {
            defaultGuild(envOrNull("swgohbots"))
        }
        extensions {
            add(::AbobaExtension)
            add(::GetCharacterStats)
            add(::AddCharToSheet)
            add(::AddCharsToSheet)
            add(::UpdateCharData)
            add(::ParseData)
            add(::AddToTeam)
            add(::GetTeam)
            add(::GetAllTeams)
            add(::AddTeamToSheet)
            add(::AddPlayerToSheet)
            add(::PoslePar)
            add(::PaintChanges)
        }
    }
    bot.start()
}
