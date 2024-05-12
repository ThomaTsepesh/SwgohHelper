package com.tsepesh.thoma

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val name: String,
    val allyCode: Int,
    val stats: Stats,
    val chars: List<Character>
)

@Serializable
data class Character(
    val idName: String,
    val stars: Int,
    val gear: String,
    val omic: Int,
    val zeta: Int,
//    val skills: List<Skill>,
//    val mods: List<Mod>
) {
    fun getCharStats(): String {
        return "${stars},${gear},${omic},${zeta}"
    }
    override fun toString(): String {
        return "$idName: Stars($stars), ${if(isRelic(gear)) "Relict(${gear.toInt()})" else "Gear($gear)"} Omics($omic), Zetas($zeta)"
    }
    fun isRelic(gear: String): Boolean{
        return gear.all { it.isDigit() }
    }
}
@Serializable
data class Skill(
    val n: String
)

@Serializable
data class Mod(
    val n: String
)

@Serializable
data class Stats(
    val omics: Int,
    val zetas: Int
)

@Serializable
data class Team(
    val charList: MutableList<String> = mutableListOf()
){
    fun addChar(charName: String){
        if (charList.size < 5)
            charList.add(charName)
    }
}