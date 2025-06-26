package com.orinugoori.tfthelper.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.orinugoori.tfthelper.core.constants.AppConstants

/**
 * 앱의 내비게이션 구조를 정의하는 함수
 * Clean Architecture의 Presentation Layer
 */
@Composable
fun TFTNavigation(
    navController: NavHostController,
    startDestination: String = AppConstants.Routes.FIRST_PAGE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 첫 번째 화면 - 증강체 선택
        composable(AppConstants.Routes.FIRST_PAGE) {
            // TODO: FirstScreen 컴포저블 추가 예정
            // FirstScreen(
            //     onNavigateToSecond = { selectedAugment ->
            //         navController.navigate("${AppConstants.Routes.SECOND_PAGE}/$selectedAugment")
            //     }
            // )
        }
        
        // 두 번째 화면 - 확률 계산
        composable("${AppConstants.Routes.SECOND_PAGE}/{firstAugment}") { backStackEntry ->
            val firstAugment = backStackEntry.arguments?.getString("firstAugment") ?: ""
            // TODO: SecondScreen 컴포저블 추가 예정
            // SecondScreen(
            //     firstAugment = firstAugment,
            //     onNavigateToThird = { secondAugment ->
            //         navController.navigate("${AppConstants.Routes.THIRD_PAGE}/$firstAugment/$secondAugment")
            //     },
            //     onNavigateBack = {
            //         navController.popBackStack()
            //     }
            // )
        }
        
        // 세 번째 화면 - 최종 확률
        composable("${AppConstants.Routes.THIRD_PAGE}/{firstAugment}/{secondAugment}") { backStackEntry ->
            val firstAugment = backStackEntry.arguments?.getString("firstAugment") ?: ""
            val secondAugment = backStackEntry.arguments?.getString("secondAugment") ?: ""
            // TODO: ThirdScreen 컴포저블 추가 예정
            // ThirdScreen(
            //     firstAugment = firstAugment,
            //     secondAugment = secondAugment,
            //     onNavigateBack = {
            //         navController.popBackStack()
            //     },
            //     onNavigateToFirst = {
            //         navController.popBackStack(AppConstants.Routes.FIRST_PAGE, false)
            //     }
            // )
        }
        
        // 증강체 리스트 화면
        composable(AppConstants.Routes.AUGMENTS_SCREEN) {
            // TODO: AugmentsScreen 컴포저블 추가 예정
            // AugmentsScreen(
            //     onNavigateBack = {
            //         navController.popBackStack()
            //     }
            // )
        }
    }
}

/**
 * 내비게이션 헬퍼 함수들
 */
object NavigationHelper {
    
    /**
     * 첫 번째 화면으로 이동
     */
    fun navigateToFirst(navController: NavHostController) {
        navController.navigate(AppConstants.Routes.FIRST_PAGE) {
            popUpTo(AppConstants.Routes.FIRST_PAGE) { inclusive = true }
        }
    }
    
    /**
     * 두 번째 화면으로 이동
     */
    fun navigateToSecond(navController: NavHostController, firstAugment: String) {
        navController.navigate("${AppConstants.Routes.SECOND_PAGE}/$firstAugment")
    }
    
    /**
     * 세 번째 화면으로 이동
     */
    fun navigateToThird(
        navController: NavHostController, 
        firstAugment: String, 
        secondAugment: String
    ) {
        navController.navigate("${AppConstants.Routes.THIRD_PAGE}/$firstAugment/$secondAugment")
    }
    
    /**
     * 증강체 리스트 화면으로 이동
     */
    fun navigateToAugments(navController: NavHostController) {
        navController.navigate(AppConstants.Routes.AUGMENTS_SCREEN)
    }
    
    /**
     * 뒤로가기
     */
    fun navigateBack(navController: NavHostController) {
        navController.popBackStack()
    }
}