package com.example.tfthelper.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

@JvmInline
value class TftHelperColor private constructor(val color: Color){
    companion object{
        val Main = Color(0xFF5C79AF)
        val Secondary = Color(0xFF0E2F6B)
        val White = Color(0xFFFFFFFF)
        val Black = Color(0xFF000000)
        val Grey = Color(0xFF9E9E9E)
        val LightGrey = Color(0xFFEBEBEB)


        val SilverGradient1 = Color(0xFFF1F1F1)
        val SilverGradient2 = Color(0xFFFFFFFF)
        val SilverGradient3 = Color(0xFFCACACA)
        val SilverGradient4 = Color(0xFFFFFFFF)
        val SilverGradient5 = Color(0xFFB7B8B0)


        val GoldGradient1 = Color(0xFFECC440)
        val GoldGradient2 = Color(0xFFFFFA8A)
        val GoldGradient3 = Color(0xFFDDAC17)
        val GoldGradient4 = Color(0xFFFFFF95)
        val GoldGradient5 = Color(0xFFC3CC40)

        val PrismGradient1 = Color(0xFFD7C3FF)
        val PrismGradient2 = Color(0xFFABEAFF)
        val PrismGradient3 = Color(0xFFECC9F1)
        val PrismGradient4 = Color(0xFFECFCFF)
        val PrismGradient5 = Color(0xFFFFB9F6)



    }
}