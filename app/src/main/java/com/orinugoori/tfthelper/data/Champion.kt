package com.orinugoori.tfthelper.data

data class Champion(
    val ability: Ability,
    val apiName: String,
    val characterName: String,
    val cost: Int,
    val icon: String,
    val name: String,
    val role: String,
    val squareIcon: String,
    val stats: Stats,
    val tileIcon: String,
    val traits: List<String>
)

data class Ability(
    val desc: String,
    val icon: String,
    val name: String,
    val variables: List<Variable>
)

data class Stats(
    val armor: Double,
    val attackSpeed: Double,
    val critChance: Double,
    val critMultiplier: Double,
    val damage: Double,
    val hp: Double,
    val initialMana: Double,
    val magicResist: Double,
    val mana: Double,
    val range: Double
)