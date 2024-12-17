package com.example.tfthelper

data class AugmentProbability(
    val firstAug : String,
    val secondAug : String,
    val thirdAug : String,
    val probability : Int
)

val augmentProbabilityList = listOf(
    AugmentProbability("프리즘", "프리즘", "프리즘", 1),
    AugmentProbability("프리즘", "프리즘", "골드", 1),
    AugmentProbability("프리즘", "골드", "프리즘", 1),
    AugmentProbability("프리즘", "골드", "골드", 2),
    AugmentProbability("프리즘", "실버", "프리즘", 1),
    AugmentProbability("프리즘", "실버", "골드", 4),
    AugmentProbability("골드", "프리즘", "프리즘", 1),
    AugmentProbability("골드", "프리즘", "골드", 10),
    AugmentProbability("골드", "프리즘", "실버", 6),
    AugmentProbability("골드", "골드", "프리즘", 3),
    AugmentProbability("골드", "골드", "골드", 22),
    AugmentProbability("골드", "실버", "프리즘", 2),
    AugmentProbability("골드", "실버", "골드", 18),
    AugmentProbability("실버", "프리즘", "프리즘", 1),
    AugmentProbability("실버", "프리즘", "골드", 5),
    AugmentProbability("실버", "골드", "프리즘", 5),
    AugmentProbability("실버", "골드", "프리즘", 5),
    AugmentProbability("실버", "골드", "골드", 12),
    AugmentProbability("실버", "실버", "프리즘", 5)
)
