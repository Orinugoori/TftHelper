package com.example.tfthelper.screen


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
import com.example.tfthelper.NextBtn
import com.example.tfthelper.Title
import com.example.tfthelper.ui.theme.TFThelperTheme
import com.example.tfthelper.ui.theme.TftHelperColor
@Composable
fun FirstPage(modifier: Modifier = Modifier, navController: NavHostController) {

    var firstAug by remember { mutableStateOf<String?>(null) }
    var secondAug by remember { mutableStateOf<String?>(null) }

    var showError by remember { mutableStateOf(false) }


    Column(
        modifier
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Title()
        SelectCurrentArg(isFirst = true) { option ->
            firstAug = option
            showError = false
        }

        SelectCurrentArg(isFirst = false) { option -> secondAug = option }


        NextBtn(
            text = "다음",
            errorText = "첫번째 증강을 선택해 주세요",
            showError = showError,
            onClick = {
                when {
                    !firstAug.isNullOrEmpty() && !secondAug.isNullOrEmpty() -> {
                        // 첫 번째와 두 번째 모두 선택 → ThirdPage로 이동
                        val selectedOptions = "$firstAug,$secondAug"
                        navController.navigate("thirdPage/$selectedOptions")
                    }

                    !firstAug.isNullOrEmpty() -> {
                        // 첫 번째만 선택 → SecondPage로 이동
                        navController.navigate("secondPage/$firstAug")
                    }

                    else -> {
                        // 첫 번째 증강이 선택되지 않았을 때 에러 메시지 표시
                        showError = true

                    }

                }
            }
        )
    }


}


@Composable
fun SelectCurrentArg(isFirst: Boolean, onOptionSelected: (String?) -> Unit) {
    Column {
        //증강 순서 표시
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = if (isFirst) "첫번째 증강" else "두번째 증강",
            style = TextStyle(fontSize = 14.sp, color = TftHelperColor.LightGrey)
        )


        //증강체 선택
        val argList = listOf("실버", "골드", "프리즘") // 증강 리스트
        ToggleBtn(options = argList, null, onOptionSelected = onOptionSelected)


    }
}


@Composable
fun ToggleBtn(
    options: List<String>,
    defaultSelection: String?,
    onOptionSelected: (String?) -> Unit
) {
    var selectedOption by remember { mutableStateOf(defaultSelection) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
    ) {
        options.forEach { option ->
            val backgroundColor = when (option) {
                selectedOption -> when (option) {
                    "실버" -> Brush.linearGradient(
                        colors = listOf(
                            TftHelperColor.SilverGradient1,
                            TftHelperColor.SilverGradient2,
                            TftHelperColor.SilverGradient3,
                            TftHelperColor.SilverGradient4,
                            TftHelperColor.SilverGradient5
                        )
                    )

                    "골드" -> Brush.linearGradient(
                        colors = listOf(
                            TftHelperColor.GoldGradient1,
                            TftHelperColor.GoldGradient2,
                            TftHelperColor.GoldGradient3,
                            TftHelperColor.GoldGradient4,
                            TftHelperColor.GoldGradient5
                        )
                    )

                    "프리즘" -> Brush.linearGradient(
                        colors = listOf(
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
                    listOf(
                        TftHelperColor.Black,
                        TftHelperColor.Black
                    )
                )
            }
            val borderColor =
                if (selectedOption == option) Color.Transparent else TftHelperColor.White
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(BorderStroke(0.4.dp, borderColor), RectangleShape) // 회색 테두리
                    .background(backgroundColor, RectangleShape) // 그라디언트 배경
                    .fillMaxHeight()
                    .clickable {
                        selectedOption = if (selectedOption == option) null else option
                        onOptionSelected(selectedOption)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (selectedOption == option) Color.White else Color.White,
                    style = if (selectedOption == option) TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    else TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
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
fun FirstScreenPreview() {
    TFThelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black)
        ) {
            val navController = rememberNavController()
            FirstPage(navController = navController)
        }
    }
}
