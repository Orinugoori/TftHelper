package com.orinugoori.tfthelper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orinugoori.tfthelper.screen.FirstPage
import com.orinugoori.tfthelper.screen.SecondPage
import com.orinugoori.tfthelper.screen.ThirdPage
import com.orinugoori.tfthelper.ui.theme.TFThelperTheme
import com.orinugoori.tfthelper.ui.theme.TftHelperColor
import com.google.android.gms.ads.MobileAds
import com.orinugoori.tfthelper.screen.AugmentPage
import kotlinx.coroutines.delay

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
                    CalculatorScreen(navController)
                }

                // 증강체 리스트 화면
                composable(BottomNavItem.Augments.route) {
                    AugmentScreen()
                }

                // 확률 계산기 세부 화면들
                composable("firstPage") {
                    FirstPage(navController = navController)
                }

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

    @Composable
    fun CalculatorScreen(navController: NavHostController) {
        FirstPage(navController = navController)
    }

    @Composable
    fun AugmentScreen() {
        val viewModel: AugmentViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsState()

        when (uiState) {
            is AugmentViewModel.UiState.Loading -> {
                LoadingScreen("증강 데이터를 불러오는 중...")
            }

            is AugmentViewModel.UiState.CacheExpired -> {
                LoadingScreen("캐시가 만료되었습니다\n새로운 데이터를 가져오는 중...")
            }

            is AugmentViewModel.UiState.LoadingFromCache -> {
                LoadingScreen("캐시에서 데이터를 불러오는 중...")
            }

            is AugmentViewModel.UiState.NetworkError -> {
                ErrorScreen(
                    title = "네트워크 연결 오류",
                    message = "인터넷 연결을 확인하고 다시 시도해주세요",
                    onRetry = { viewModel.retryLoading() }
                )
            }

            is AugmentViewModel.UiState.Error -> {
                ErrorScreen(
                    title = "오류 발생",
                    message = (uiState as AugmentViewModel.UiState.Error).message,
                    onRetry = { viewModel.retryLoading() }
                )
            }

            is AugmentViewModel.UiState.Success -> {
                AugmentPage(viewModel = viewModel)
            }
        }
    }

    @Composable
    private fun LoadingScreen(message: String) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    color = TftHelperColor.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun ErrorScreen(
        title: String,
        message: String,
        onRetry: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = TftHelperColor.White,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = message,
                    color = TftHelperColor.Grey,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("다시 시도")
                }
            }
        }
    }
}