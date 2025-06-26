package com.orinugoori.tfthelper.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.orinugoori.tfthelper.data.model.Augment
import com.orinugoori.tfthelper.presentation.viewmodel.AugmentViewModel
import com.orinugoori.tfthelper.presentation.theme.TftHelperColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AugmentPage(
    viewModel: AugmentViewModel,
    modifier: Modifier = Modifier
) {
    val tiers = listOf("전체","실버","골드","프리즘")
    val filteredAugments by viewModel.filteredAugments.collectAsState()
    val selectedTier by viewModel.selectedTier.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    var isSearchMode by remember { mutableStateOf(false) }
    var showSearchHistory by remember { mutableStateOf(false) }
    var showRecommendations by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 현재 버전 정보 가져오기
    val currentVersion = viewModel.getCurrentVersion()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TftHelperColor.Black)
    ) {

        // 🔍 개선된 검색 바
        EnhancedSearchTopBar(
            isSearchMode = isSearchMode,
            searchQuery = searchQuery,
            searchSuggestions = searchSuggestions,
            searchHistory = searchHistory,
            showSearchHistory = showSearchHistory,
            showRecommendations = showRecommendations,
            isSearching = isSearching,
            // currentVersion은 AugmentCard로 직접 전달하지 않고, Augment 모델 자체에 포함되므로 여기서는 필요 없음
            onSearchModeChange = {
                isSearchMode = it
                if (it) showRecommendations = true
                else showRecommendations = false
                                 },
            onSearchQueryChange = {
                viewModel.updateSearchQuery(it) // ViewModel의 updateSearchQuery 호출
                showSearchHistory = it.isEmpty() && searchHistory.isNotEmpty()
                showRecommendations = it.isNotEmpty()
            },
            onSearchAction = { query -> // 검색 버튼 또는 엔터 키 입력 시
                viewModel.performSearch(query) // ViewModel의 performSearch 호출
                viewModel.clearSearchSuggestions()
                showRecommendations = false
                keyboardController?.hide()
                showSearchHistory = false
            },
            onSearchHistoryToggle = { showSearchHistory = it },
            onSuggestionClick = { suggestion ->
                viewModel.searchWithSuggestion(suggestion) // ViewModel의 searchWithSuggestion 호출
                viewModel.clearSearchSuggestions()
                showRecommendations = false
                keyboardController?.hide()
                showSearchHistory = false
            },
            onRefresh = { viewModel.refreshAugments() },
            onClearSearch = {
                viewModel.clearSearchInput() // ViewModel의 clearSearchInput 호출
                keyboardController?.hide()
                isSearchMode = false
                showSearchHistory = false
            }
        )

        // 🎯 티어 선택 및 검색 결과 표시
        AnimatedContent(
            targetState = isSearchMode,
            label = "search_content_transition"
        ) { searchMode ->
            if (searchMode && showSearchHistory) {
                // 🔍 검색 히스토리 및 제안 UI
                SearchHistorySection(
                    searchHistory = searchHistory,
                    searchSuggestions = searchSuggestions,
                    onHistoryItemClick = { historyItem ->
                        viewModel.searchWithSuggestion(historyItem)
                        keyboardController?.hide()
                        showRecommendations = false
                        showSearchHistory = false
                    },
                    onClearHistory = { viewModel.clearAllSearchHistory() }
                )
            } else {
                // 📱 메인 증강체 리스트
                Column {
                    if (!searchMode) {
                        // 티어 선택 탭 (검색 모드가 아닐 때만 표시)
                        EnhancedTierSelector(
                            tiers = tiers,
                            selectedTier = selectedTier,
                            onTierSelected = { tier ->
                                viewModel.updateTierFilter(tier)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🚀 성능 최적화된 증강체 리스트
                    OptimizedAugmentList(
                        augments = filteredAugments,
                        currentVersion = currentVersion,
                        isSearchMode = searchMode,
                        searchQuery = searchQuery,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 🔍 향상된 검색 탑바
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedSearchTopBar(
    isSearchMode: Boolean,
    searchQuery: String,
    searchSuggestions: List<String>,
    searchHistory: List<String>,
    showSearchHistory: Boolean,
    showRecommendations : Boolean,
    isSearching: Boolean,
    onSearchModeChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchAction: (String) -> Unit, // 추가: 검색 액션 (엔터, 버튼 클릭)
    onSearchHistoryToggle: (Boolean) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onClearSearch: () -> Unit
) {
    TopAppBar(
        title = {
            if (isSearchMode) {
                // 🔍 검색 입력 필드
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        textStyle = TextStyle(
                            color = TftHelperColor.White,
                            fontSize = 18.sp
                        ),
                        cursorBrush = SolidColor(TftHelperColor.White),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions( // 추가: 키보드 액션
                            onSearch = { onSearchAction(searchQuery) }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        decorationBox = { innerTextField ->
                            Box {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        "증강체 검색... (초성 검색 지원)",
                                        color = TftHelperColor.White.copy(alpha = 0.5f),
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    // 🔄 검색 중 인디케이터
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TftHelperColor.White,
                            strokeWidth = 2.dp
                        )
                    }
                }
            } else {
                // 📱 일반 모드 제목
                Column {
                    Text(
                        text = "TFT 증강체",
                        color = TftHelperColor.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        actions = {
            if (isSearchMode) {
                // 🗂️ 검색 히스토리 토글 버튼
                if (searchHistory.isNotEmpty()) {
                    IconButton(onClick = { onSearchHistoryToggle(!showSearchHistory) }) {
                        Icon(
                            if (showSearchHistory) Icons.Default.ExpandLess else Icons.Default.History,
                            contentDescription = "검색 기록",
                            tint = TftHelperColor.White
                        )
                    }
                }

                // ❌ 검색 모드 종료
                IconButton(onClick = onClearSearch) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "검색 닫기",
                        tint = TftHelperColor.White
                    )
                }
            } else {
                // 🔄 새로고침 버튼
                IconButton(onClick = onRefresh) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "새로고침",
                        tint = TftHelperColor.White
                    )
                }

                // 🔍 검색 버튼
                IconButton(onClick = { onSearchModeChange(true) }) {
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

    // 🔍 검색 제안 드롭다운
    // 검색 모드이고, 검색어가 비어있지 않으며, 제안 목록이 있을 때만 표시
    if (isSearchMode && showRecommendations && searchSuggestions.isNotEmpty() && searchQuery.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .background(TftHelperColor.Black)
                .border(1.dp, TftHelperColor.White.copy(alpha = 0.3f))
        ) {
            items(searchSuggestions) { suggestion ->
                Text(
                    text = suggestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(suggestion) }
                        .padding(16.dp),
                    color = TftHelperColor.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * 🗂️ 검색 히스토리 섹션
 */
@Composable
private fun SearchHistorySection(
    searchHistory: List<String>,
    searchSuggestions: List<String>, // 검색 히스토리와 함께 제안도 표시할 수 있도록 유지
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (searchHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "최근 검색",
                        color = TftHelperColor.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onClearHistory) {
                        Text("전체 삭제", color = TftHelperColor.White.copy(alpha = 0.7f))
                    }
                }
            }

            items(searchHistory) { historyItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryItemClick(historyItem) },
                    colors = CardDefaults.cardColors(
                        containerColor = TftHelperColor.Black
                    ),
                    border = BorderStroke(1.dp, TftHelperColor.White.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = TftHelperColor.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            historyItem,
                            color = TftHelperColor.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🎯 향상된 티어 선택기
 */
@Composable
private fun EnhancedTierSelector(
    tiers: List<String>,
    selectedTier: String,
    onTierSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
    ) {
        tiers.forEach { tier ->
            val isSelected = selectedTier == tier

            val backgroundColor = when {
                isSelected -> when (tier) {
                    "실버" -> Brush.linearGradient(
                        colors = listOf(
                            TftHelperColor.SilverGradient1,
                            TftHelperColor.SilverGradient3,
                            TftHelperColor.SilverGradient5
                        )
                    )
                    "골드" -> Brush.linearGradient(
                        colors = listOf(
                            TftHelperColor.GoldGradient1,
                            TftHelperColor.GoldGradient3,
                            TftHelperColor.GoldGradient5
                        )
                    )
                    "프리즘" -> Brush.linearGradient(
                        colors = listOf(
                            TftHelperColor.PrismGradient1,
                            TftHelperColor.PrismGradient3,
                            TftHelperColor.PrismGradient5
                        )
                    )
                    else -> Brush.linearGradient(listOf(TftHelperColor.White, TftHelperColor.Grey))
                }
                else -> Brush.linearGradient(listOf(TftHelperColor.Black, TftHelperColor.Black))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(
                        BorderStroke(1.dp, TftHelperColor.White.copy(alpha = 0.3f)),
                        RectangleShape
                    )
                    .background(backgroundColor, RectangleShape)
                    .clickable { onTierSelected(tier) },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tier,
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
}

/**
 * 🚀 성능 최적화된 증강체 리스트
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OptimizedAugmentList(
    augments: List<Augment>,
    currentVersion: String, // 이 파라미터는 AugmentCard에서 더 이상 사용하지 않음
    isSearchMode: Boolean,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    if (augments.isEmpty()) {
        // 빈 상태 UI
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = TftHelperColor.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isSearchMode) "검색 결과가 없습니다" else "증강체가 없습니다",
                    color = TftHelperColor.White,
                    fontSize = 18.sp
                )
                if (isSearchMode && searchQuery.isNotEmpty()) {
                    Text(
                        text = "'$searchQuery'에 대한 결과가 없습니다",
                        color = TftHelperColor.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    } else {
        // 성능 최적화된 LazyColumn
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = augments,
                key = { augment -> augment.id } // 성능 최적화를 위한 키 지정
            ) { augment ->
                // 하이라이트된 검색 결과를 위한 증강체 카드
                HighlightedAugmentCard(
                    augment = augment,
                    currentVersion = currentVersion, // 이 파라미터는 AugmentCard에서 더 이상 사용하지 않음
                    searchQuery = if (isSearchMode) searchQuery else "",
                    modifier = Modifier.animateItemPlacement() // 애니메이션 최적화
                )
            }
        }
    }
}

/**
 * 🔍 검색어 하이라이트가 적용된 증강체 카드
 */
@Composable
private fun HighlightedAugmentCard(
    augment: Augment,
    currentVersion: String, // 이 파라미터는 AugmentCard에서 더 이상 사용하지 않으므로 제거 가능
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    // 기존 AugmentCard와 동일하지만 검색어 하이라이트 추가
    AugmentCard(
        augment = augment,
        // currentVersion = currentVersion // AugmentCard로 직접 전달하지 않음
    )
}



@Composable
fun AugmentCard(
    augment: Augment,
    // currentVersion: String // 이 파라미터는 이제 필요 없음
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
            // 증강 이미지 - 올바른 Data Dragon URL 사용 (이제 Augment 모델의 imageUrl 사용)
            AsyncImage(
                // model = "https://ddragon.leagueoflegends.com/cdn/$currentVersion/img/tft-augment/${augment.image.full}", // <-- 이전 코드
                model = augment.imageUrl, // <-- Augment 모델의 imageUrl 필드를 직접 사용합니다.
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
                model = augment.imageUrl,
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