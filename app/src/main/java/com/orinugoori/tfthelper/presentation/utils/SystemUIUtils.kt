package com.orinugoori.tfthelper.presentation.utils

import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * 시스템 UI 관련 유틸리티 함수들
 */

/**
 * 상태바 숨기기
 */
@Composable
fun HideStatusBar() {
    val systemUiController = rememberSystemUiController()
    systemUiController.isStatusBarVisible = false
}