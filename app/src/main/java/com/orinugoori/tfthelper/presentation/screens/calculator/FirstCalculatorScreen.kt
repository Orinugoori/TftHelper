package com.orinugoori.tfthelper.presentation.screens.calculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.orinugoori.tfthelper.NextBtn
import com.orinugoori.tfthelper.Title
import com.orinugoori.tfthelper.presentation.theme.TFTHelperTheme
import com.orinugoori.tfthelper.presentation.theme.TftHelperColor
import com.orinugoori.tfthelper.core.constants.AppConstants

@Composable
fun FirstCalculatorScreen(
    modifier: Modifier = Modifier, 
    navController: NavHostController
) {
    var firstAugment by remember { mutableStateOf<String?>(null) }
    var secondAugment by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        // 타이틀
        Title(
            title = AppConstants.Messages.SELECT_FIRST_AUGMENT,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 첫 번째 증강 선택
        Text(
            text = AppConstants.Messages.FIRST_AUGMENT_LABEL,
            color = TftHelperColor.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AugmentTierSelector(
            selectedOption = firstAugment,
            onOptionSelected = { 
                firstAugment = it
                showError = false
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 두 번째 증강 선택 (첫 번째 선택 후 표시)
        if (firstAugment != null) {
            Text(
                text = AppConstants.Messages.SECOND_AUGMENT_LABEL,
                color = TftHelperColor.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AugmentTierSelector(
                selectedOption = secondAugment,
                onOptionSelected = { secondAugment = it },
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // 에러 메시지
        if (showError) {
            Text(
                text = AppConstants.Messages.SELECT_FIRST_AUGMENT,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // 다음 버튼
        NextBtn(
            text = AppConstants.Messages.NEXT_BUTTON,
            isEnabled = firstAugment != null,
            onClick = {
                if (firstAugment != null) {
                    // 선택된 증강 정보를 다음 화면으로 전달
                    navController.navigate(
                        "${AppConstants.Routes.SECOND_PAGE}/${firstAugment}/${secondAugment ?: ""}"
                    )
                } else {
                    showError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        )
    }
}

/**
 * 증강체 티어 선택 컴포넌트
 */
@Composable
private fun AugmentTierSelector(
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        AppConstants.AugmentTiers.SELECTION_TIERS.forEach { option ->
            val backgroundColor = when (option) {
                selectedOption -> when (option) {
                    AppConstants.AugmentTiers.SILVER -> Brush.linearGradient(
                        listOf(
                            TftHelperColor.SilverGradient1,
                            TftHelperColor.SilverGradient2
                        )
                    )
                    AppConstants.AugmentTiers.GOLD -> Brush.linearGradient(
                        listOf(
                            TftHelperColor.GoldGradient1,
                            TftHelperColor.GoldGradient2
                        )
                    )
                    AppConstants.AugmentTiers.PRISM -> Brush.linearGradient(
                        listOf(
                            TftHelperColor.PrismGradient1,
                            TftHelperColor.PrismGradient2,
                            TftHelperColor.PrismGradient3,
                            TftHelperColor.PrismGradient4,
                            TftHelperColor.PrismGradient5,
                        )
                    )
                    else -> Brush.linearGradient(listOf(TftHelperColor.Black))
                }
                else -> Brush.linearGradient(
                    listOf(TftHelperColor.Black, TftHelperColor.Black)
                )
            }
            
            val borderColor = if (selectedOption == option) Color.Transparent else TftHelperColor.White
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(BorderStroke(0.4.dp, borderColor), RectangleShape)
                    .background(backgroundColor, RectangleShape)
                    .fillMaxHeight()
                    .clickable {
                        val newSelection = if (selectedOption == option) null else option
                        onOptionSelected(newSelection)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = Color.White,
                    style = if (selectedOption == option) {
                        TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    } else {
                        TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(name = "Small Device", widthDp = 320, heightDp = 480)
@Preview(name = "Normal Device", widthDp = 360, heightDp = 640)
@Preview(name = "Large Device", widthDp = 600, heightDp = 960)
@Preview(
    name = "Galaxy Flip Preview",
    device = "spec:shape=Normal,width=1080,height=2636,unit=px,dpi=420"
)
@Composable
fun FirstCalculatorScreenPreview() {
    TFTHelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black)
        ) {
            val navController = rememberNavController()
            FirstCalculatorScreen(navController = navController)
        }
    }
}
