package com.orinugoori.tfthelper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orinugoori.tfthelper.AugmentViewModel

/**
 * 로딩 인디케이터
 */
@Composable
fun LoadingIndicator(
    message: String = "데이터를 불러오는 중...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 에러 상태 UI
 */
@Composable
fun ErrorStateUI(
    uiState: AugmentViewModel.UiState,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (uiState) {
                    is AugmentViewModel.UiState.NetworkError -> {
                        ErrorContent(
                            icon = Icons.Default.WifiOff,
                            title = "네트워크 연결 오류",
                            message = "인터넷 연결을 확인하고 다시 시도해주세요.",
                            primaryButtonText = "다시 시도",
                            onPrimaryClick = onRetry,
                            secondaryButtonText = "새로고침",
                            onSecondaryClick = onRefresh
                        )
                    }
                    is AugmentViewModel.UiState.Error -> {
                        ErrorContent(
                            icon = Icons.Default.Error,
                            title = "오류 발생",
                            message = uiState.message,
                            primaryButtonText = "다시 시도",
                            onPrimaryClick = onRetry,
                            secondaryButtonText = "새로고침",
                            onSecondaryClick = onRefresh
                        )
                    }
                    is AugmentViewModel.UiState.CacheExpired -> {
                        ErrorContent(
                            icon = Icons.Default.Update,
                            title = "데이터 업데이트 필요",
                            message = "캐시된 데이터가 만료되었습니다. 최신 데이터를 가져오시겠습니까?",
                            primaryButtonText = "업데이트",
                            onPrimaryClick = onRefresh,
                            secondaryButtonText = "다시 시도",
                            onSecondaryClick = onRetry
                        )
                    }
                    else -> {
                        // 기본 에러 상태
                        ErrorContent(
                            icon = Icons.Default.ErrorOutline,
                            title = "문제가 발생했습니다",
                            message = "알 수 없는 오류가 발생했습니다.",
                            primaryButtonText = "다시 시도",
                            onPrimaryClick = onRetry
                        )
                    }
                }
            }
        }
    }
}

/**
 * 에러 콘텐츠 컴포넌트
 */
@Composable
private fun ErrorContent(
    icon: ImageVector,
    title: String,
    message: String,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = MaterialTheme.colorScheme.error
    )

    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onPrimaryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(primaryButtonText)
        }

        if (secondaryButtonText != null && onSecondaryClick != null) {
            OutlinedButton(
                onClick = onSecondaryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(secondaryButtonText)
            }
        }
    }
}

/**
 * 성공 스낵바
 */
@Composable
fun SuccessSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        action = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        },
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.inversePrimary
            )
            Text(message)
        }
    }
}

/**
 * 빈 상태 UI
 */
@Composable
fun EmptyStateUI(
    icon: ImageVector = Icons.Default.SearchOff,
    title: String = "결과가 없습니다",
    message: String = "검색 조건을 변경해보세요.",
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            if (actionButtonText != null && onActionClick != null) {
                Button(onClick = onActionClick) {
                    Text(actionButtonText)
                }
            }
        }
    }
}


@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 시간 차이를 사용자 친화적 형식으로 변환
 */
private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60 * 1000 -> "방금 전"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}분 전"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}시간 전"
        else -> "${diff / (24 * 60 * 60 * 1000)}일 전"
    }
}