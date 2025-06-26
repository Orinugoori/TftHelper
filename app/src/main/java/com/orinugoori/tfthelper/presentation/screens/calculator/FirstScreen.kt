package com.orinugoori.tfthelper.presentation.screens.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orinugoori.tfthelper.presentation.components.AppTitle
import com.orinugoori.tfthelper.presentation.components.NextButton
import com.orinugoori.tfthelper.presentation.screens.calculator.components.AugmentSelector
import com.orinugoori.tfthelper.core.constants.AppConstants

/**
 * 첫 번째 증강체 선택 화면
 * Clean Architecture의 Presentation Layer
 */
@Composable
fun FirstScreen(
    onNavigateToSecond: (String) -> Unit,
    onNavigateToAugments: () -> Unit
) {
    var selectedAugment by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        
        // 앱 제목
        AppTitle()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 안내 텍스트
        Text(
            text = "첫 번째 증강체를 선택하세요",
            style = MaterialTheme.typography.headlineSmall
        )
        
        // 증강체 선택기
        AugmentSelector(
            selectedAugment = selectedAugment,
            onAugmentSelected = { 
                selectedAugment = it
                showError = false
            },
            placeholder = "첫 번째 증강체 선택",
            onNavigateToAugments = onNavigateToAugments
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 다음 버튼
        NextButton(
            text = "다음 단계",
            errorText = "첫 번째 증강체를 선택해주세요",
            showError = showError,
            onClick = {
                if (selectedAugment.isNotEmpty()) {
                    onNavigateToSecond(selectedAugment)
                } else {
                    showError = true
                }
            }
        )
    }
}