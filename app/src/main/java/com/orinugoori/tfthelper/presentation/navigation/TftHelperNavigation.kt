package com.orinugoori.tfthelper.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.orinugoori.tfthelper.core.constants.AppConstants
import com.orinugoori.tfthelper.presentation.screens.splash.SplashScreen
import com.orinugoori.tfthelper.presentation.screens.calculator.FirstCalculatorScreen
import com.orinugoori.tfthelper.presentation.screens.augments.AugmentsListScreen

/**
 * 앱 전체 네비게이션 구성
 */
@Composable
fun TftHelperNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // 스플래시 화면
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(AppConstants.Routes.FIRST_PAGE) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        
        // 첫 번째 계산기 화면
        composable(AppConstants.Routes.FIRST_PAGE) {
            FirstCalculatorScreen(navController = navController)
        }
        
        // 두 번째 계산기 화면 (확률 결과)
        composable("${AppConstants.Routes.SECOND_PAGE}/{firstAugment}/{secondAugment}") { backStackEntry ->
            val firstAugment = backStackEntry.arguments?.getString("firstAugment")
            val secondAugment = backStackEntry.arguments?.getString("secondAugment")?.takeIf { it.isNotEmpty() }
            
            // SecondCalculatorScreen 구현 필요
            // SecondCalculatorScreen(
            //     firstAugment = firstAugment,
            //     secondAugment = secondAugment,
            //     navController = navController
            // )
        }
        
        // 증강체 리스트 화면
        composable(AppConstants.Routes.AUGMENTS) {
            AugmentsListScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
