package com.example.tfthelper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.tfthelper.ui.theme.TftHelperColor

@Composable
fun Title(modifier: Modifier = Modifier) {
    Text(
        text = "TFT 증강체 확률 계산기",
        modifier = modifier,
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TftHelperColor.White
        )
    )
}


@Composable
fun NextBtn(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(TftHelperColor.White),
        border = BorderStroke(1.dp, color = TftHelperColor.White)
    ) {
        Text(text = text, color = TftHelperColor.Black)
    }
}

@Composable
fun ShowSelectedArg(modifier: Modifier = Modifier, isFirst: Boolean, prevSelectedArg: String) {
    Column(modifier = modifier) {
        Text(
            text = if (isFirst) "첫번째 증강" else "두번째 증강",
            style = TextStyle(
                fontSize = 14.sp,
                color = TftHelperColor.LightGrey
            )
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = prevSelectedArg,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TftHelperColor.White
            )
        )


    }
}

@Composable
fun CustomDropdownMenu(
    options: Set<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("전체") }
    val scrollState = rememberLazyListState()

    Column {
        OutlinedButton(
            shape = RoundedCornerShape(5.dp),
            onClick = { expanded = !expanded },
            modifier = Modifier.width(120.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    selectedOption,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TftHelperColor.White
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TftHelperColor.Grey
                )
            }
        }

        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false }
            ) {
                Box {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .width(120.dp)
                            .heightIn(max = 400.dp)
                            .background(TftHelperColor.White)
                            .drawVerticalScrollbar(scrollState)
                    ) {
                        items(options.toList()) { option ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedOption = option
                                        expanded = false
                                        onOptionSelected(option)
                                    }
                                    .padding(8.dp)
                            ) {
                                Text(option, color = TftHelperColor.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

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

