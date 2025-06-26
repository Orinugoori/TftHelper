package com.orinugoori.tfthelper.presentation.screens.calculator

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.orinugoori.tfthelper.AdViewModel
import com.orinugoori.tfthelper.core.constants.AppConstants
import com.orinugoori.tfthelper.core.constants.ColorConstants
import com.orinugoori.tfthelper.presentation.components.AppTitle
import com.orinugoori.tfthelper.presentation.components.NextButton
import com.orinugoori.tfthelper.presentation.components.SelectedAugmentDisplay
import com.orinugoori.tfthelper.presentation.screens.calculator.components.ProbabilityDisplaySection
import com.orinugoori.tfthelper.presentation.viewmodel.ProbabilityViewModel
import com.orinugoori.tfthelper.ui.theme.TFThelperTheme

@Composable
fun ThirdScreen(
    selectedOptions: String,
    navController: NavHostController,
    adViewModel: AdViewModel,
    modifier: Modifier = Modifier
) {
    val probabilityViewModel: ProbabilityViewModel = viewModel()
    val options = selectedOptions.split(",")
    val firstOption = options[0]
    val secondOption = options[1]
    
    val context = LocalContext.current
    val activity = context as? Activity
    
    var showError by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AppTitle()
        
        Row(Modifier.fillMaxWidth()) {
            SelectedAugmentDisplay(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                isFirst = true,
                selectedAugment = firstOption
            )
            
            SelectedAugmentDisplay(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                isFirst = false,
                selectedAugment = secondOption
            )
        }
        
        val thirdProbabilities = probabilityViewModel.calculateThirdAugmentProbabilities(firstOption, secondOption)
        ProbabilityDisplaySection(
            title = "세번째 증강 확률",
            probabilities = thirdProbabilities
        )
        
        NextButton(
            text = AppConstants.Messages.BACK_TO_FIRST,
            onClick = {
                if (adViewModel.interstitialAd != null && activity != null) {
                    adViewModel.interstitialAd?.show(activity)
                } else {
                    navController.navigate(AppConstants.Routes.FIRST_PAGE) {
                        popUpTo(AppConstants.Routes.FIRST_PAGE) { inclusive = true }
                    }
                }
            }
        )
        
        if (activity != null) {
            adViewModel.loadInterstitialAd(activity) {
                navController.navigate(AppConstants.Routes.FIRST_PAGE) {
                    popUpTo(AppConstants.Routes.FIRST_PAGE) { inclusive = true }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThirdScreenPreview() {
    TFThelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorConstants.BLACK)
        ) {
            val navController = rememberNavController()
            ThirdScreen(
                navController = navController,
                selectedOptions = "프리즘,프리즘",
                adViewModel = AdViewModel()
            )
        }
    }
}