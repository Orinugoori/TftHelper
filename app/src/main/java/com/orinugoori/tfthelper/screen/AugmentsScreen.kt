package com.orinugoori.tfthelper.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import com.orinugoori.tfthelper.Augment
import com.orinugoori.tfthelper.AugmentViewModel
import com.orinugoori.tfthelper.CustomDropdownMenu
import com.orinugoori.tfthelper.ui.theme.TFThelperTheme
import com.orinugoori.tfthelper.ui.theme.TftHelperColor
import kotlinx.coroutines.launch


@Composable
fun AugmentPage(
    viewModel: AugmentViewModel,
    modifier: Modifier = Modifier
) {

    val tiers = listOf("전체","실버","골드","프리즘")

    val keywordList = viewModel.keywordList.collectAsState()
    val filteredAugments by viewModel.filteredAugments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedTier = viewModel.selectedTier.collectAsState().value
    var selectedKeyword = viewModel.selectedKeyword.collectAsState().value

    Column(
        modifier
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

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
                options = keywordList.value,
                selectedOption = selectedKeyword,
                onOptionSelected = { option ->
                    selectedKeyword = option
                    viewModel.filterAugmentsByKeyword(option)
                }
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        // 에러 메시지 표시
        errorMessage?.let { error ->
            Column {
                Text(
                    text = error,
                    color = TftHelperColor.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = { 
                        viewModel.clearError()
                        viewModel.refreshAugments() 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TftHelperColor.White)
                ) {
                    Text("다시 시도", color = TftHelperColor.Black)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 로딩 상태 표시
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = TftHelperColor.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "증강 데이터를 불러오는 중...",
                        color = TftHelperColor.White,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            AugmentPageWithPager(
                filteredAugments = filteredAugments,
                tiers = tiers,
                selectedTier = selectedTier,
                onTierSelected = { tier ->
                    selectedTier = tier
                    viewModel.filterAugmentsByTier(tier)
                }
            )
        }
    }
}

@Composable
fun FilterSpinner(options: Set<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    CustomDropdownMenu(
        options = options,
        onOptionSelected = { option->
                onOptionSelected(option)
                expanded = false
        },
        expanded = expanded,
        selectedOption = selectedOption,
        onExpandChange = { isExpanded -> expanded = isExpanded },
    )
}

@Composable
fun AugmentPageWithPager(filteredAugments: List<Augment>, tiers: List<String>, selectedTier: String, onTierSelected: (String) -> Unit) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tiers.size })
    val coroutineScope = rememberCoroutineScope()

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
        ) { _->
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
                        AugmentItem(augment = augment)
                    }
                }
            }
        }
    }

    // Pager와 필터링 동기화
    LaunchedEffect(pagerState.currentPage) {
        val currentTier = tiers[pagerState.currentPage]
        if (currentTier != selectedTier) {
            onTierSelected(currentTier) // 티어 선택 변경 외부로 전달
        }
    }
}

@Composable
fun AugmentItem(augment: Augment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                TftHelperColor.Black.copy(alpha = 0.3f),
                RectangleShape
            )
            .padding(12.dp)
    ) {
        // 이미지 URL 수정 - 시즌 15 버전 사용
        AsyncImage(
            model = "https://ddragon.leagueoflegends.com/cdn/15.1.1/img/tft-augment/${augment.image.full}",
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            // 증강 이름과 티어 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = augment.name,
                    style = TextStyle(
                        color = TftHelperColor.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                
                // 티어 배지
                val tierColor = when (augment.tier) {
                    "실버" -> TftHelperColor.SilverGradient3
                    "골드" -> TftHelperColor.GoldGradient3
                    "프리즘" -> TftHelperColor.PrismGradient3
                    else -> TftHelperColor.Grey
                }
                
                Text(
                    text = augment.tier,
                    style = TextStyle(
                        color = TftHelperColor.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .background(tierColor, RectangleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 설명
            Text(
                text = augment.description,
                style = TextStyle(
                    color = TftHelperColor.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            )
            
            // 키워드 표시
            if (augment.keyword.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    augment.keyword.take(3).forEach { keyword ->
                        Text(
                            text = keyword,
                            style = TextStyle(
                                color = TftHelperColor.Black,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier
                                .background(TftHelperColor.LightGrey, RectangleShape)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(BorderStroke(0.4.dp, TftHelperColor.White), RectangleShape)
                    .background(backgroundColor, RectangleShape)
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
@Preview(name = "Normal Device", widthDp = 360, heightDp = 640)
@Preview(name = "Large Device", widthDp = 600, heightDp = 960)
@Preview(
    name = "Galaxy Flip Preview",
    device = "spec:shape=Normal,width=1080,height=2636,unit=px,dpi=420"
)
@Composable
fun AugmentScreenPreview() {
    TFThelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black)
        ) {
            AugmentPage(viewModel = AugmentViewModel())
        }
    }
}
