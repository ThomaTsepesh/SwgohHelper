package com.tsepesh.thoma

import com.tsepesh.thoma.bot.BotDataHelper

suspend fun main() {
//    val list = CrawlerSWgohGG.getCharsAlly(849418263u)
//    list.forEach { c-> println(c) }
    BotDataHelper.parseData(849418263)
}