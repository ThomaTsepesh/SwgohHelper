package com.tsepesh.thoma

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val name: String,
    val allyCode: Int,
    val stats: Stats,
    val chars: List<Character>
) {
    fun getChar(charName: String): Character? {
        return chars.firstOrNull { it.idName == charName }
    }
}

@Serializable
data class Character(
    val idName: String,
    val stars: Int,
    val charGP: Int,
    val gear: String,
    val omic: Int,
    val zeta: Int,
//    val skills: List<Skill>,
//    val mods: List<Mod>
) {
    fun getCharStats(): String {
        return "${stars},${charGP},${gear},${omic},${zeta}"
    }
    fun getCharListStats(): MutableList<String>{
        return mutableListOf(stars.toString(), charGP.toString(), gear, omic.toString(), zeta.toString())
    }

    override fun toString(): String {
        return "$idName: Stars($stars), ${if (isRelic(gear)) "Relict(${gear.toInt()})" else "Gear($gear)"} Omics($omic), Zetas($zeta)"
    }

    private fun isRelic(gear: String): Boolean {
        return gear.all { it.isDigit() }
    }
}

//@Serializable
//data class Skill(
//    val n: String
//)

//@Serializable
//data class Mod(
//    val n: String
//)

@Serializable
data class Stats(
    val omics: Int,
    val zetas: Int
)

@Serializable
data class Team(
    val charList: MutableList<String> = mutableListOf()
) {
    fun addChar(charName: String) {
        if (charList.size < 5)
            charList.add(charName)
    }
    override fun toString(): String {
        return charList.joinToString(",")
    }
    companion object {
        fun toTeam(team: String): Team {
            return Team(team.split(",").toMutableList())
        }
    }
}