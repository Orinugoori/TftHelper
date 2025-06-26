package com.orinugoori.tfthelper.presentation.screens.augments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.orinugoori.tfthelper.core.constants.AppConstants
import com.orinugoori.tfthelper.data.model.UiAugment
import com.orinugoori.tfthelper.presentation.theme.TftHelperColor
import com.orinugoori.tfthelper.presentation.theme.TFTHelperTheme
import com.orinugoori.tfthelper.presentation.viewmodel.AugmentViewModel

/**
 * 증강체 리스트를 표시하는 메인 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AugmentsListScreen(
    onBackClick: () -> Unit,
    viewModel: AugmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTier by remember { mutableStateOf(AppConstants.AugmentTiers.ALL) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // 검색 제안
    val searchSuggestions = remember(searchQuery, uiState.augments) {
        if (searchQuery.length >= 2) {
            uiState.augments
                .filter { it.name.contains(searchQuery, ignoreCase = true) }
                .map { it.name }
                .distinct()
                .take(AppConstants.Search.MAX_SUGGESTIONS)
        } else {
            emptyList()
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadAugments()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TftHelperColor.Black)
    ) {
        // 상단 앱바
        AugmentsTopAppBar(
            isSearchMode = isSearchMode,
            searchQuery = searchQuery,
            searchSuggestions = searchSuggestions,
            onSearchModeChange = { isSearchMode = it },
            onSearchQueryChange = { 
                searchQuery = it
                if (it.isNotEmpty()) {
                    viewModel.searchAugments(it)
                } else {
                    viewModel.loadAugments()
                }
            },
            onSuggestionClick = { suggestion ->
                searchQuery = suggestion
                viewModel.searchAugments(suggestion)
                viewModel.addToSearchHistory(suggestion)
                keyboardController?.hide()
            },
            onBackClick = {
                if (isSearchMode) {
                    isSearchMode = false
                    searchQuery = ""
                    viewModel.loadAugments()
                } else {
                    onBackClick()
                }
            },
            onRefresh = { viewModel.refreshAugments() }
        )
        
        when {
            // 검색 모드이고 검색어가 비어있을 때 검색 기록 표시
            isSearchMode && searchQuery.isEmpty() -> {
                SearchHistorySection(
                    searchHistory = searchHistory,
                    onHistoryItemClick = { query ->
                        searchQuery = query
                        viewModel.searchAugments(query)
                        keyboardController?.hide()
                    },
                    onClearHistory = { viewModel.clearSearchHistory() }
                )
            }
            
            // 일반 모드 또는 검색 결과 표시
            else -> {
                // 티어 필터
                if (!isSearchMode) {
                    TierFilterSection(
                        selectedTier = selectedTier,
                        onTierSelected = { tier ->
                            selectedTier = tier
                            viewModel.filterByTier(tier)
                        }
                    )
                }
                
                // 증강체 리스트
                AugmentListContent(
                    uiState = uiState,
                    onRetry = { viewModel.loadAugments() }
                )
            }
        }
    }
}

/**
 * 상단 앱바 컴포넌트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AugmentsTopAppBar(
    isSearchMode: Boolean,
    searchQuery: String,
    searchSuggestions: List<String>,
    onSearchModeChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = {
            if (isSearchMode) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { 
                        Text(
                            AppConstants.Search.SEARCH_PLACEHOLDER,
                            color = TftHelperColor.White.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TftHelperColor.White,
                        unfocusedTextColor = TftHelperColor.White,
                        focusedBorderColor = TftHelperColor.White,
                        unfocusedBorderColor = TftHelperColor.White.copy(alpha = 0.6f)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "TFT 증강체 리스트",
                    color = TftHelperColor.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = TftHelperColor.White
                )
            }
        },
        actions = {
            if (isSearchMode && searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "검색어 지우기",
                        tint = TftHelperColor.White
                    )
                }
            } else {
                IconButton(onClick = onRefresh) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "새로고침",
                        tint = TftHelperColor.White
                    )
                }

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
}

/**
 * 검색 히스토리 섹션
 */
@Composable
private fun SearchHistorySection(
    searchHistory: List<String>,
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
                        text = "최근 검색어",
                        color = TftHelperColor.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onClearHistory) {
                        Text(
                            text = "전체 삭제",
                            color = TftHelperColor.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            items(searchHistory) { query ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryItemClick(query) },
                    colors = CardDefaults.cardColors(
                        containerColor = TftHelperColor.Black.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(1.dp, TftHelperColor.White.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = query,
                        modifier = Modifier.padding(16.dp),
                        color = TftHelperColor.White,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            item {
                Text(
                    text = "검색 기록이 없습니다",
                    color = TftHelperColor.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 티어 필터 섹션
 */
@Composable
private fun TierFilterSection(
    selectedTier: String,
    onTierSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AppConstants.AugmentTiers.ALL_TIERS) { tier ->
            TierFilterChip(
                tier = tier,
                isSelected = tier == selectedTier,
                onClick = { onTierSelected(tier) }
            )
        }
    }
}

/**
 * 티어 필터 칩 컴포넌트
 */
@Composable
private fun TierFilterChip(
    tier: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> when (tier) {
            AppConstants.AugmentTiers.SILVER -> Brush.linearGradient(
                listOf(TftHelperColor.SilverGradient1, TftHelperColor.SilverGradient2)
            )
            AppConstants.AugmentTiers.GOLD -> Brush.linearGradient(
                listOf(TftHelperColor.GoldGradient1, TftHelperColor.GoldGradient2)
            )
            AppConstants.AugmentTiers.PRISM -> Brush.linearGradient(
                listOf(
                    TftHelperColor.PrismGradient1,
                    TftHelperColor.PrismGradient2,
                    TftHelperColor.PrismGradient3
                )
            )
            else -> Brush.linearGradient(listOf(TftHelperColor.White, TftHelperColor.White))
        }
        else -> Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                1.dp,
                if (isSelected) Color.Transparent else TftHelperColor.White.copy(alpha = 0.5f),
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tier,
            color = if (isSelected) Color.Black else TftHelperColor.White,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

/**
 * 증강체 리스트 콘텐츠
 */
@Composable
private fun AugmentListContent(
    uiState: AugmentViewModel.UiState,
    onRetry: () -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = TftHelperColor.White)
                    Text(
                        text = AppConstants.Messages.LOADING_AUGMENTS,
                        color = TftHelperColor.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = uiState.error,
                        color = Color.Red,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TftHelperColor.White
                        )
                    ) {
                        Text(
                            text = "다시 시도",
                            color = TftHelperColor.Black
                        )
                    }
                }
            }
        }
        
        uiState.augments.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "증강체 데이터가 없습니다",
                    color = TftHelperColor.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.augments) { augment ->
                    AugmentCard(augment = augment)
                }
            }
        }
    }
}

/**
 * 증강체 카드 컴포넌트
 */
@Composable
private fun AugmentCard(augment: UiAugment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TftHelperColor.Black.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, TftHelperColor.White.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 증강체 이미지
            AsyncImage(
                model = augment.imageUrl,
                contentDescription = augment.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 증강체 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = augment.name,
                    color = TftHelperColor.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 티어 표시
                TierBadge(tier = augment.tier)
                
                if (augment.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = augment.description,
                        color = TftHelperColor.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

/**
 * 티어 배지 컴포넌트
 */
@Composable
private fun TierBadge(tier: String) {
    val backgroundColor = when (tier) {
        AppConstants.AugmentTiers.SILVER -> TftHelperColor.SilverGradient1
        AppConstants.AugmentTiers.GOLD -> TftHelperColor.GoldGradient1
        AppConstants.AugmentTiers.PRISM -> TftHelperColor.PrismGradient1
        else -> TftHelperColor.White.copy(alpha = 0.3f)
    }
    
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = tier,
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AugmentsListScreenPreview() {
    TFTHelperTheme {
        AugmentsListScreen(onBackClick = {})
    }
}
