package com.orinugoori.tfthelper.presentation.screen.augments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orinugoori.tfthelper.presentation.components.ErrorStateUI
import com.orinugoori.tfthelper.presentation.components.LoadingIndicator
import com.orinugoori.tfthelper.presentation.screen.AugmentPage
import com.orinugoori.tfthelper.presentation.viewmodel.AugmentViewModel


@Composable
fun AugmentMainScreen() {
    val viewModel: AugmentViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is AugmentViewModel.UiState.Loading -> {
            LoadingIndicator("증강 데이터를 불러오는 중...")
        }

        is AugmentViewModel.UiState.CacheExpired -> {
            LoadingIndicator("캐시가 만료되었습니다\n새로운 데이터를 가져오는 중...")
        }

        is AugmentViewModel.UiState.LoadingFromCache -> {
            LoadingIndicator("캐시에서 데이터를 불러오는 중...")
        }

        is AugmentViewModel.UiState.NetworkError -> {
            ErrorStateUI(
                uiState = uiState,
                onRetry = { viewModel.retryLoading() },
                onRefresh = {viewModel.refreshAugments()}
            )
        }

        is AugmentViewModel.UiState.Error -> {
            ErrorStateUI(
                uiState = uiState,
                onRetry = { viewModel.retryLoading() },
                onRefresh = {viewModel.refreshAugments()}
            )
        }

        is AugmentViewModel.UiState.Success -> {
            AugmentPage(viewModel = viewModel)
        }
    }
}