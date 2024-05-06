package com.tsepesh.thoma

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val name: String,
    val allyCode: Int,
    val playerLink: String,
//    val stats: List<Stats>
//    val chars: List<Character>
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
)

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