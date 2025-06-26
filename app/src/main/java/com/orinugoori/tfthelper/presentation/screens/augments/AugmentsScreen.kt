package com.orinugoori.tfthelper.presentation.screens.augments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orinugoori.tfthelper.domain.model.Augment
import com.orinugoori.tfthelper.presentation.components.SearchBar
import com.orinugoori.tfthelper.presentation.screens.augments.components.AugmentCard
import com.orinugoori.tfthelper.presentation.screens.augments.components.TierFilterTabs

/**
 * 증강체 리스트 화면
 * Clean Architecture의 Presentation Layer
 */
@Composable
fun AugmentsScreen(
    augments: List<Augment>,
    searchQuery: String,
    selectedTier: String?,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onTierFilterChange: (String?) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        
        // 검색 바
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = "증강체 검색... (초성 검색 지원)",
            onNavigateBack = onNavigateBack
        )
        
        // 티어 필터 탭
        TierFilterTabs(
            selectedTier = selectedTier,
            onTierSelected = onTierFilterChange
        )
        
        // 증강체 리스트
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(augments) { augment ->
                    AugmentCard(
                        augment = augment,
                        onClick = {
                            // TODO: 증강체 선택 처리
                        }
                    )
                }
                
                if (augments.isEmpty() && !isLoading) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "검색 결과가 없습니다",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "다른 검색어를 시도해보세요",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}