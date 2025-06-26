package com.orinugoori.tfthelper.core.constants

import androidx.compose.ui.graphics.Color

/**
 * 앱에서 사용되는 색상 상수들
 */
object ColorConstants {
    
    // 기본 색상
    val WHITE = Color(0xFFFFFFFF)
    val BLACK = Color(0xFF000000)
    val GREY = Color(0xFF9E9E9E)
    val LIGHT_GREY = Color(0xFFEBEBEB)
    val RED = Color(0xFFC94242)
    val TRANSPARENT = Color.Transparent
    
    // 실버 그라디언트
    object Silver {
        val GRADIENT_1 = Color(0xFFF1F1F1)
        val GRADIENT_2 = Color(0xFFFFFFFF)
        val GRADIENT_3 = Color(0xFFCACACA)
        val GRADIENT_4 = Color(0xFFFFFFFF)
        val GRADIENT_5 = Color(0xFFB7B8B0)
        
        val ALL_GRADIENTS = listOf(GRADIENT_1, GRADIENT_2, GRADIENT_3, GRADIENT_4, GRADIENT_5)
    }
    
    // 골드 그라디언트
    object Gold {
        val GRADIENT_1 = Color(0xFFECC440)
        val GRADIENT_2 = Color(0xFFFFFA8A)
        val GRADIENT_3 = Color(0xFFDDAC17)
        val GRADIENT_4 = Color(0xFFFFFF95)
        val GRADIENT_5 = Color(0xFFC3CC40)
        
        val ALL_GRADIENTS = listOf(GRADIENT_1, GRADIENT_2, GRADIENT_3, GRADIENT_4, GRADIENT_5)
    }
    
    // 프리즘 그라디언트
    object Prism {
        val GRADIENT_1 = Color(0xFFD7C3FF)
        val GRADIENT_2 = Color(0xFFABEAFF)
        val GRADIENT_3 = Color(0xFFECC9F1)
        val GRADIENT_4 = Color(0xFFECFCFF)
        val GRADIENT_5 = Color(0xFFFFB9F6)
        
        val ALL_GRADIENTS = listOf(GRADIENT_1, GRADIENT_2, GRADIENT_3, GRADIENT_4, GRADIENT_5)
    }
}