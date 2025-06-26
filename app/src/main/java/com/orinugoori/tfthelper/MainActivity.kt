package com.orinugoori.tfthelper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orinugoori.tfthelper.presentation.screen.carculator.FirstPage
import com.orinugoori.tfthelper.presentation.screen.carculator.SecondPage
import com.orinugoori.tfthelper.presentation.screen.carculator.ThirdPage
import com.orinugoori.tfthelper.presentation.theme.TFThelperTheme
import com.orinugoori.tfthelper.presentation.theme.TftHelperColor
import com.google.android.gms.ads.MobileAds
import com.orinugoori.tfthelper.presentation.components.HideStatusBarScreen
import com.orinugoori.tfthelper.presentation.screen.augments.AugmentMainScreen
import com.orinugoori.tfthelper.presentation.viewmodel.AdViewModel

class MainActivity : ComponentActivity() {

    private val adViewModel: AdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 광고 초기화
        MobileAds.initialize(this)

        adViewModel.loadInterstitialAd(this) {
            Log.d("광고테스트", "첫페이지로 이동해야합니다.")
        }

        setContent {
            TFThelperTheme {
                HideStatusBarScreen()
                MainScreen(adViewModel)
            }
        }
    }

    sealed class BottomNavItem(val title: String, val route: String, val icon: ImageVector) {
        data object Calculator : BottomNavItem("확률 계산기", "calculator", Icons.Filled.Search)
        data object Augments : BottomNavItem("증강체 리스트", "augments", Icons.Filled.Menu)
    }



    @Composable
    fun MainScreen(adViewModel: AdViewModel) {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Calculator.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .background(TftHelperColor.Black)
            ) {
                // 확률 계산기 화면들
                composable(BottomNavItem.Calculator.route) {
                    FirstPage(navController = navController)
                }

                // 증강체 리스트 화면
                composable(BottomNavItem.Augments.route) {
                    AugmentMainScreen()
                }
                // 확률 계산기 세부 화면들
                composable("secondPage/{firstAug}") { backStackEntry ->
                    val selectedOption = backStackEntry.arguments?.getString("firstAug") ?: ""
                    SecondPage(navController = navController, firstAug = selectedOption)
                }

                composable("thirdPage/{selectedOptions}") { backStackEntry ->
                    val selectedOptions = backStackEntry.arguments?.getString("selectedOptions") ?: ""
                    ThirdPage(
                        navController = navController,
                        selectedOptions = selectedOptions,
                        adViewModel = adViewModel
                    )
                }
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        val items = listOf(
            BottomNavItem.Calculator,
            BottomNavItem.Augments
        )

        NavigationBar(
            containerColor = TftHelperColor.Black
        ) {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            items.forEach { screen ->
                NavigationBarItem(
                    label = {
                        Text(
                            text = screen.title,
                            style = TextStyle(color = TftHelperColor.White)
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        if (currentRoute != screen.route) {
                            try {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } catch (e: Exception) {
                                Log.e("Navigation", "화면 전환 실패: ${screen.route}", e)
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = null,
                            tint = if (currentRoute == screen.route) TftHelperColor.White else TftHelperColor.Grey
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}