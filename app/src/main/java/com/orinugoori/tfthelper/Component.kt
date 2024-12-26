package com.orinugoori.tfthelper

import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
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
import com.orinugoori.tfthelper.ui.theme.TftHelperColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController



// 상태바 숨기기
@Composable
fun HideStatusBarScreen() {
    val systemUiController = rememberSystemUiController()

    systemUiController.isStatusBarVisible = false
}


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
fun NextBtn(text: String, errorText: String?, showError: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            if (showError) TftHelperColor.Red else TftHelperColor.White
        )
    ) {
        Text(
            text = if (showError) errorText ?: text else text,
            color = TftHelperColor.Black
        )
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
    onOptionSelected: (String) -> Unit,
    expanded: Boolean,
    selectedOption: String,
    onExpandChange: (Boolean) -> Unit,
) {

    var scrollState by remember { mutableStateOf<LazyListState?>(null) }

    DisposableEffect(expanded) {
        if (expanded) {
            scrollState = LazyListState()
            Log.d("CustomDropdownMenu", "LazyListState created: $scrollState")
        }

        onDispose {
            if (expanded) {
                Log.d("CustomDropdownMenu", "LazyListState disposed: $scrollState")
                scrollState = null // 참조 제거
            }
        }
    }


    Column {
        OutlinedButton(
            shape = RoundedCornerShape(5.dp),
            onClick = { onExpandChange(!expanded) },
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
                onDismissRequest = { onExpandChange(false) }
            ) {
                Box {
                    LazyColumn(
                        state = scrollState!!, // scrollState가 null일 경우 NullPointerException 방지
                        modifier = Modifier
                            .width(120.dp)
                            .heightIn(max = 400.dp)
                            .background(TftHelperColor.White)
                            .drawVerticalScrollbar(scrollState ?: rememberLazyListState())
                    ) {
                        items(options.toList()) { option ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionSelected(option)
                                        onExpandChange(false)
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


//@Composable
//fun CustomDropdownMenu(
//    options: Set<String>,
//    onOptionSelected: (String) -> Unit,
//    expanded : Boolean,
//    selectedOption : String,
//    onExpandChange: (Boolean) -> Unit,
//    scrollState: LazyListState = remember { LazyListState() }
//) {
//
//    DisposableEffect(scrollState) {
//        Log.d("CustomDropdownMenu", "LazyListState created: $scrollState")
//
//        onDispose {
//            Log.d("CustomDropdownMenu", "LazyListState disposed: $scrollState")
//        }
//    }
//
//
//    Column {
//        OutlinedButton(
//            shape = RoundedCornerShape(5.dp),
//            onClick = { onExpandChange(!expanded) },
//            modifier = Modifier.width(120.dp),
//            contentPadding = PaddingValues(horizontal = 4.dp)
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(
//                    selectedOption,
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 14.sp,
//                    color = TftHelperColor.White
//                )
//                Icon(
//                    Icons.Default.ArrowDropDown,
//                    contentDescription = null,
//                    tint = TftHelperColor.Grey
//                )
//            }
//        }
//
//        if (expanded) {
//            Popup(
//                onDismissRequest = { onExpandChange(false) }
//            ) {
//                Box {
//                    LazyColumn(
//                        state = scrollState,
//                        modifier = Modifier
//                            .width(120.dp)
//                            .heightIn(max = 400.dp)
//                            .background(TftHelperColor.White)
//                            .drawVerticalScrollbar(scrollState)
//                    ) {
//                        items(options.toList()) { option ->
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable {
//                                        onOptionSelected(option)
//                                        onExpandChange(false)
//                                    }
//                                    .padding(8.dp)
//                            ) {
//                                Text(option, color = TftHelperColor.Black)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//


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

