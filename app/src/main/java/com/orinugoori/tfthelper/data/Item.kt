package com.orinugoori.tfthelper.data

data class Item(
    val apiName: String,
    val associatedTraits: List<String>,
    val composition: List<String>,
    val desc: String,
    val effects: Effects,
    val from: Any,
    val icon: String,
    val id: Any,
    val incompatibleTraits: List<String>,
    val name: String,
    val unique: Boolean
)