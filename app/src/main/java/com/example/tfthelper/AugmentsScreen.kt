package com.example.tfthelper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.tfthelper.ui.theme.TFThelperTheme
import com.example.tfthelper.ui.theme.TftHelperColor
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch


@Composable
fun AugmentPage(
    viewModel: AugmentViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    var selectedTier by remember { mutableStateOf("전체") }
    val tiers = listOf("전체","실버","골드","프리즘")

    Column(
        modifier
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        val keywordList = viewModel.keywordList.collectAsState().value

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "증강 리스트",
                style = TextStyle(
                    TftHelperColor.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            FilterSpinner(
                optionList = keywordList,
                onOptionSelected = { option ->
                    viewModel.filterAugmentsByKeyword(option)
                }
            )

        }


        Spacer(modifier = Modifier.size(8.dp))

        AugmentPageWithPager(viewModel =viewModel , tiers = tiers)

    }
}


@Composable
fun FilterSpinner(optionList: Set<String>, onOptionSelected: (String) -> Unit) {

    CustomDropdownMenu(options = optionList, onOptionSelected = onOptionSelected)

}@OptIn(ExperimentalPagerApi::class)
@Composable
fun AugmentPageWithPager(viewModel: AugmentViewModel, tiers: List<String>) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tiers.size })
    val coroutineScope = rememberCoroutineScope()

    val filteredAugments by viewModel.filteredAugments.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // AugmentSelectButton과 HorizontalPager 연동
        AugmentSelectButton(
            options = tiers,
            pagerState = pagerState,
            onOptionSelected = { selectedTier ->
                val targetPage = tiers.indexOf(selectedTier)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(targetPage)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // HorizontalPager를 활용한 페이지 전환
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            // 현재 페이지에 해당하는 티어에 따른 증강 데이터 가져오기
            val currentTier = tiers[page]

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredAugments.isEmpty()) {
                    item {
                        Text(
                            text = "해당되는 증강이 없습니다.",
                            color = TftHelperColor.White,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(filteredAugments) { augment ->
                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Image(
                                painter = rememberImagePainter("https://ddragon.leagueoflegends.com/cdn/14.24.1/img/tft-augment/${augment.image.full}"),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = augment.name,
                                    style = TextStyle(TftHelperColor.White)
                                )
                                Text(
                                    text = augment.description,
                                    style = TextStyle(
                                        TftHelperColor.White,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Pager와 필터링 동기화
    LaunchedEffect(pagerState.currentPage) {
        viewModel.filterAugmentsByTier(tiers[pagerState.currentPage])
    }
}



@Composable
fun AugmentSelectButton(
    options: List<String>,
    pagerState : PagerState,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
    ) {
        options.forEachIndexed { index, option ->
            val backgroundColor = when (pagerState.currentPage == index) {
                true -> when (option) {
                    "실버" -> Brush.linearGradient(
                        colors = listOf(
                            TftHelperColor.SilverGradient1,
                            TftHelperColor.SilverGradient2,
                            TftHelperColor.SilverGradient3,
                            TftHelperColor.SilverGradient4,
                            TftHelperColor.SilverGradient5
                        )
                    )

                    "골드" -> Brush.linearGradient(
                        colors = listOf(
                            TftHelperColor.GoldGradient1,
                            TftHelperColor.GoldGradient2,
                            TftHelperColor.GoldGradient3,
                            TftHelperColor.GoldGradient4,
                            TftHelperColor.GoldGradient5
                        )
                    )

                    "프리즘" -> Brush.linearGradient(
                        colors = listOf(
                            TftHelperColor.PrismGradient1,
                            TftHelperColor.PrismGradient2,
                            TftHelperColor.PrismGradient3,
                            TftHelperColor.PrismGradient4,
                            TftHelperColor.PrismGradient5,
                        )
                    )

                    else -> Brush.linearGradient(listOf(TftHelperColor.Grey, TftHelperColor.Grey))
                }

                false -> Brush.linearGradient(
                    listOf(
                        TftHelperColor.Black,
                        TftHelperColor.Black
                    )
                )
            }
           // val borderColor =
            //    if (selectedOption == option) Color.Transparent else TftHelperColor.White

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(BorderStroke(0.4.dp, TftHelperColor.White), RectangleShape) // 회색 테두리
                    .background(backgroundColor, RectangleShape) // 그라디언트 배경
                    .fillMaxHeight()
                    .clickable {
                        onOptionSelected(option)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = TftHelperColor.White,
                    style = TextStyle(
                        fontWeight = if(pagerState.currentPage == index) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }

}





@Preview(showBackground = true)
@Composable
fun AugmentScreenPreview() {
    TFThelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black)
        ) {
            val navController = rememberNavController()
            AugmentPage(navController = navController, viewModel = AugmentViewModel())
        }
    }
}
