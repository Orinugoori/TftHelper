package com.orinugoori.tfthelper.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import com.orinugoori.tfthelper.presentation.theme.TftHelperColor



fun Modifier.drawVerticalScrollbar(scrollState: LazyListState): Modifier {
    return this.drawWithContent {
        drawContent() // 기존 LazyColumn 콘텐츠를 그리기

        // 스크롤바 계산
        val totalItems = scrollState.layoutInfo.totalItemsCount
        val visibleItems = scrollState.layoutInfo.visibleItemsInfo.size
        val scrollbarHeight = size.height * (visibleItems.toFloat() / totalItems)
        val scrollbarOffset = size.height * (scrollState.firstVisibleItemIndex +
                scrollState.firstVisibleItemScrollOffset / scrollState.layoutInfo.viewportEndOffset.toFloat()) / totalItems

        // 스크롤바 그리기
        drawRect(
            color = TftHelperColor.Grey.copy(alpha = 0.8f),
            topLeft = androidx.compose.ui.geometry.Offset(
                x = size.width - 4.dp.toPx(),
                y = scrollbarOffset
            ),
            size = androidx.compose.ui.geometry.Size(width = 5.dp.toPx(), height = scrollbarHeight)
        )
    }
}