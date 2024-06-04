package com.tsepesh.thoma.bot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand

class PoslePar: Extension() {
    override val name = "kogda?"

    override suspend fun setup() {
        publicSlashCommand(){
            name = "kogda"
            description = "posle par"
            action {
                respond {
                    content = "После пар... обязательно.."
                }
            }
        }
    }
}