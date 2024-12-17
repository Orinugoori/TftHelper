package com.example.tfthelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tfthelper.ui.theme.TFThelperTheme
import com.example.tfthelper.ui.theme.TftHelperColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TFThelperTheme {
                MainScreen() // MainScreen에서 모든 네비게이션을 처리
            }
        }
    }
}

sealed class BottomNavItem(val title: String, val route: String, val icon : ImageVector) {
    object Calculator : BottomNavItem("확률 계산기", "calculator", Icons.Filled.Search)
    object Augments : BottomNavItem("증강체 리스트", "augments", Icons.Filled.List)
}


@Composable
fun MainScreen() {
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
                val selectedOptions = backStackEntry.arguments?.getString("selectedOptions") ?: ""
                ThirdPage(navController = navController, selectedOptions = selectedOptions)
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










