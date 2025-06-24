package com.orinugoori.tfthelper.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orinugoori.tfthelper.AugmentViewModel
import com.orinugoori.tfthelper.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AugmentsPage(
    viewModel: AugmentViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val augments by viewModel.filteredAugments.collectAsState()
    val selectedTier by viewModel.selectedTier.collectAsState()
    val selectedKeyword by viewModel.selectedKeyword.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val keywordList by viewModel.keywordList.collectAsState()

    var showCacheInfo by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // 성공 메시지 처리
    LaunchedEffect(uiState) {
        if (uiState is AugmentViewModel.UiState.Success && (uiState as AugmentViewModel.UiState.Success).message.isNotEmpty()) {
            successMessage = (uiState as AugmentViewModel.UiState.Success).message
            showSuccessMessage = true
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 상단 툴바
        TopAppBar(
            title = { Text("증강 가이드") },
            actions = {
                IconButton(onClick = { showCacheInfo = !showCacheInfo }) {
                    Icon(Icons.Default.Info, contentDescription = "캐시 정보")
                }
                IconButton(onClick = { viewModel.refreshAugments() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                }
            }
        )

        // 캐시 정보 카드 (토글)
        if (showCacheInfo) {
            CacheInfoCard(
                cacheInfo = viewModel.getCacheInfo(),
                onClearCache = {
                    viewModel.refreshAugments()
                    showCacheInfo = false
                },
                modifier = Modifier.padding(16.dp)
            )
        }

        when (uiState) {
            is AugmentViewModel.UiState.Loading -> {
                LoadingIndicator(
                    message = "증강 데이터를 불러오는 중...",
                    modifier = Modifier.weight(1f)
                )
            }

            is AugmentViewModel.UiState.LoadingFromCache -> {
                LoadingIndicator(
                    message = "캐시에서 데이터를 불러오는 중...",
                    modifier = Modifier.weight(1f)
                )
            }

            is AugmentViewModel.UiState.Error,
            is AugmentViewModel.UiState.NetworkError,
            is AugmentViewModel.UiState.CacheExpired -> {
                ErrorStateUI(
                    uiState = uiState,
                    onRetry = { viewModel.retryLoading() },
                    onRefresh = { viewModel.refreshAugments() },
                    modifier = Modifier.weight(1f)
                )
            }

            is AugmentViewModel.UiState.Success -> {
                // 성공 상태 - 필터와 리스트 표시
                Column(modifier = Modifier.weight(1f)) {
                    // 검색 바
                    SearchAndFilterSection(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        onClearSearch = { viewModel.clearSearch() },
                        selectedTier = selectedTier,
                        onTierChange = { viewModel.updateTierFilter(it) },
                        selectedKeyword = selectedKeyword,
                        onKeywordChange = { viewModel.updateKeywordFilter(it) },
                        keywordList = keywordList,
                        onClearAllFilters = { viewModel.clearAllFilters() }
                    )

                    // 결과 표시
                    if (augments.isEmpty()) {
                        EmptyStateUI(
                            icon = Icons.Default.SearchOff,
                            title = "검색 결과가 없습니다",
                            message = "다른 검색어나 필터를 사용해보세요.",
                            actionButtonText = "필터 초기화",
                            onActionClick = { viewModel.clearAllFilters() },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        AugmentList(
                            augments = augments,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 성공 스낵바
        if (showSuccessMessage) {
            SuccessSnackbar(
                message = successMessage,
                onDismiss = { showSuccessMessage = false },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    selectedTier: String,
    onTierChange: (String) -> Unit,
    selectedKeyword: String,
    onKeywordChange: (String) -> Unit,
    keywordList: Set<String>,
    onClearAllFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 검색 바
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("증강 검색") },
                placeholder = { Text("증강 이름으로 검색...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(Icons.Default.Clear, contentDescription = "검색 초기화")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 티어 필터
                FilterDropdown(
                    label = "티어",
                    selectedValue = selectedTier,
                    options = listOf("전체", "실버", "골드", "프리즘"),
                    onValueChange = onTierChange,
                    modifier = Modifier.weight(1f)
                )

                // 키워드 필터
                FilterDropdown(
                    label = "키워드",
                    selectedValue = selectedKeyword,
                    options = listOf("전체") + keywordList.sorted(),
                    onValueChange = onKeywordChange,
                    modifier = Modifier.weight(1f)
                )
            }

            // 필터 초기화 버튼
            if (selectedTier != "전체" || selectedKeyword != "전체" || searchQuery.isNotEmpty()) {
                OutlinedButton(
                    onClick = onClearAllFilters,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.FilterAltOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("모든 필터 초기화")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AugmentList(
    augments: List<com.orinugoori.tfthelper.Augment>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = augments,
            key = { it.id }
        ) { augment ->
            AugmentCard(augment = augment)
        }
    }
}

@Composable
private fun AugmentCard(
    augment: com.orinugoori.tfthelper.Augment,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = augment.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                if (augment.tier.isNotEmpty()) {
                    AssistChip(
                        onClick = { },
                        label = { Text(augment.tier) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = when (augment.tier) {
                                "실버" -> MaterialTheme.colorScheme.surfaceVariant
                                "골드" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                                "프리즘" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    )
                }
            }

            if (augment.description.isNotEmpty()) {
                Text(
                    text = augment.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (augment.keyword.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(augment.keyword) { keyword ->
                        FilterChip(
                            onClick = { },
                            label = { Text(keyword, style = MaterialTheme.typography.bodySmall) },
                            selected = false
                        )
                    }
                }
            }
        }
    }
}