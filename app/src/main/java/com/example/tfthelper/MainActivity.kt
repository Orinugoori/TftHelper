package com.example.tfthelper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
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
import com.example.tfthelper.ui.theme.TFThelperTheme
import com.example.tfthelper.ui.theme.TftHelperColor
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

    private val adViewModel: AdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        MobileAds.initialize(this)

        adViewModel.loadInterstitialAd(this) {
            Log.d("광고테스트", "첫페이지로 이동해야합니다.")
        }

        setContent {
            TFThelperTheme {
                MainScreen(adViewModel)
            }
        }
    }


    sealed class BottomNavItem(val title: String, val route: String, val icon: ImageVector) {
        object Calculator : BottomNavItem("확률 계산기", "calculator", Icons.Filled.Search)
        object Augments : BottomNavItem("증강체 리스트", "augments", Icons.Filled.List)
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
                Modifier
                    .padding(innerPadding)
                    .background(TftHelperColor.Black)
            ) {
                composable(BottomNavItem.Calculator.route) { CalculatorScreen(navController) }
                composable(BottomNavItem.Augments.route) { AugmentScreen(navController) }

                composable("firstPage") {
                    FirstPage(navController = navController)
                }
                composable("secondPage/{firstAug}") { backStackEntry ->
                    val selectedOption = backStackEntry.arguments?.getString("firstAug") ?: ""
                    SecondPage(navController = navController, firstAug = selectedOption)
                }
                composable("thirdPage/{selectedOptions}") { backStackEntry ->
                    val selectedOptions =
                        backStackEntry.arguments?.getString("selectedOptions") ?: ""
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
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
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
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
                        indicatorColor = Color.Transparent // 인디케이터 색상을 배경색과 같게 설정
                    )
                )
            }
        }
    }


    @Composable
    fun CalculatorScreen(navController: NavHostController) {
        FirstPage(navController = navController)
    }

    @Composable
    fun AugmentScreen(navController: NavHostController) {
        AugmentPage(navController = navController, viewModel = AugmentViewModel())
    }


}








