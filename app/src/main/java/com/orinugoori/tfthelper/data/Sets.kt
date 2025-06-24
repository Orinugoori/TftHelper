package com.orinugoori.tfthelper.data

data class Sets(
    val `1`: X1,
    val `11`: X11,
    val `12`: X12,
    val `13`: X13,
    val `3`: X3,
    val `5`: X5
)

data class X1(
    val champions: List<Champion>,
    val name: String,
    val traits: List<TraitX>
)

data class X3(
    val champions: List<Champion>,
    val name: String,
    val traits: List<TraitXXXXX>
)

data class X5(
    val champions: List<Champion>,
    val name: String,
    val traits: List<TraitXXXXXX>
)

data class X11(
    val champions: List<Champion>,
    val name: String,
    val traits: List<TraitXX>
)

data class X12(
    val champions: List<Champion>,
    val name: String,
    val traits: List<TraitXXX>
)

data class X13(
    val champions: List<Champion>,
    val name: String,
    val traits: List<TraitXXXX>
)