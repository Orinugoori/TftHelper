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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.orinugoori.tfthelper.Augment
import com.orinugoori.tfthelper.AugmentViewModel
import com.orinugoori.tfthelper.ui.theme.TftHelperColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AugmentPage(
    viewModel: AugmentViewModel,
    modifier: Modifier = Modifier
) {
    val tiers = listOf("전체","실버","골드","프리즘")
    val filteredAugments by viewModel.filteredAugments.collectAsState()
    var selectedTier = viewModel.selectedTier.collectAsState().value
    var isSearchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 현재 버전 정보 가져오기
    val currentVersion = viewModel.getCurrentVersion()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TftHelperColor.Black)
    ) {
        // 커스텀 탑 앱바
        TopAppBar(
            title = {
                if (isSearchMode) {
                    // 검색 모드
                    BasicTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            // 실시간 검색
                            viewModel.searchAugments(it)
                        },
                        textStyle = TextStyle(
                            color = TftHelperColor.White,
                            fontSize = 18.sp
                        ),
                        cursorBrush = SolidColor(TftHelperColor.White),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                // 검색 실행
                                viewModel.searchAugments(searchText)
                                keyboardController?.hide()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        "증강 검색...",
                                        color = TftHelperColor.White.copy(alpha = 0.5f),
                                        fontSize = 18.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                } else {
                    // 일반 모드 - Data Dragon 표시
                    Text(
                        text = "Data Dragon (v$currentVersion)",
                        color = TftHelperColor.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            },
            actions = {
                if (isSearchMode) {
                    // 검색 모드일 때 닫기 버튼
                    IconButton(
                        onClick = {
                            isSearchMode = false
                            searchText = ""
                            keyboardController?.hide()
                            viewModel.clearSearch()
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "검색 닫기",
                            tint = TftHelperColor.White
                        )
                    }
                } else {
                    // 일반 모드일 때 새로고침 버튼과 검색 버튼 표시
                    IconButton(
                        onClick = {
                            viewModel.refreshAugments()
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "새로고침",
                            tint = TftHelperColor.White
                        )
                    }
                    IconButton(
                        onClick = { isSearchMode = true }
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "검색",
                            tint = TftHelperColor.White
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = TftHelperColor.Black
            )
        )

        // 증강 페이저
        AugmentPageWithPager(
            filteredAugments = filteredAugments,
            tiers = tiers,
            selectedTier = selectedTier,
            onTierSelected = { tier ->
                selectedTier = tier
                viewModel.filterAugmentsByTier(tier)
            },
            currentVersion = currentVersion,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun AugmentPageWithPager(
    filteredAugments: List<Augment>,
    tiers: List<String>,
    selectedTier: String,
    onTierSelected: (String) -> Unit,
    currentVersion: String,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tiers.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 티어 선택 탭
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

        // 페이저 콘텐츠
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { _ ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TftHelperColor.Black),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        AugmentCard(
                            augment = augment,
                            currentVersion = currentVersion
                        )
                    }
                }
            }
        }
    }

    // Pager와 필터링 동기화
    LaunchedEffect(pagerState.currentPage) {
        val currentTier = tiers[pagerState.currentPage]
        if (currentTier != selectedTier) {
            onTierSelected(currentTier)
        }
    }
}

@Composable
fun AugmentCard(
    augment: Augment,
    currentVersion: String
) {
    // 티어별 그라디언트 테두리 색상 정의
    val borderBrush = when (augment.tier) {
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
                TftHelperColor.PrismGradient5
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(TftHelperColor.White, TftHelperColor.White)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = borderBrush,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(2.dp), // 테두리 두께
        colors = CardDefaults.cardColors(
            containerColor = TftHelperColor.Black
        ),
        shape = RoundedCornerShape(10.dp) // 내부 카드는 조금 더 작은 radius
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 증강 이미지 - 올바른 Data Dragon URL 사용
            AsyncImage(
                model = "https://ddragon.leagueoflegends.com/cdn/$currentVersion/img/tft-augment/${augment.image.full}",
                contentDescription = augment.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 증강 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                            .background(tierColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 증강 설명 (정리된 설명)
                if (augment.description.isNotEmpty() && augment.description != "설명이 없습니다.") {
                    Text(
                        text = augment.description,
                        style = TextStyle(
                            color = TftHelperColor.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    )
                } else {
                    Text(
                        text = "설명이 제공되지 않습니다.",
                        style = TextStyle(
                            color = TftHelperColor.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AugmentSelectButton(
    options: List<String>,
    pagerState: PagerState,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(TftHelperColor.Black),
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = pagerState.currentPage == index
            val backgroundColor = when {
                isSelected -> when (option) {
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
                else -> Brush.linearGradient(listOf(TftHelperColor.Black, TftHelperColor.Black))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        BorderStroke(1.dp, TftHelperColor.White.copy(alpha = 0.3f)),
                        RectangleShape
                    )
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
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}