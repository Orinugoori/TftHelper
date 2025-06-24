package com.orinugoori.tfthelper.data

data class SetData(
    val augments: List<String>,
    val champions: List<Champion>,
    val items: List<String>,
    val mutator: String,
    val name: String,
    val number: Int,
    val traits: List<Trait>
)