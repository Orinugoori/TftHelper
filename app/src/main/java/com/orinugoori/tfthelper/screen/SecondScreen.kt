package com.orinugoori.tfthelper.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.orinugoori.tfthelper.CaculateProbabilities
import com.orinugoori.tfthelper.NextBtn
import com.orinugoori.tfthelper.ShowSelectedArg
import com.orinugoori.tfthelper.Title
import com.orinugoori.tfthelper.ui.theme.TFThelperTheme
import com.orinugoori.tfthelper.ui.theme.TftHelperColor


@Composable
fun SecondPage(
    modifier: Modifier = Modifier,
    firstAug: String,
    navController: NavController
) {

    var secondAug by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    val calculateLogic = CaculateProbabilities()

    Column(
        modifier
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Title()

        Row(Modifier.fillMaxWidth()) {
            ShowSelectedArg(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                isFirst = true,
                prevSelectedArg = firstAug
            )


            SelectSecondArgs(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onOptionSelected = {option ->
                    secondAug = option
                    showError = false
                }
            )

        }

        val secondAugProbabilities =
            calculateLogic.calculateNormalizedSecondProbabilities(firstAug)
        ShowSecondAugProbabilities(probabilitis = secondAugProbabilities)

        val secondThirdAugProbabilities =
            calculateLogic.calculateSecondThirdProbabilities(firstAug)
        ShowSecondThirdAugProbabilities(secondThirdAugProbabilities)

        NextBtn(
            text = "다음",
            errorText = "두번째 증강을 선택해 주세요",
            showError= showError,
            onClick = {
                if(secondAug != null){
                    val selectedOptions = "$firstAug,$secondAug"
                    navController.navigate("thirdPage/$selectedOptions")
                }else{
                    showError = true
                }
            }
        )
    }

}



@Composable
fun SelectSecondArgs(modifier: Modifier,onOptionSelected: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(
            text = "두번째 증강",
            style = TextStyle(
                fontSize = 14.sp,
                color = TftHelperColor.LightGrey
            )
        )

        Spacer(modifier = Modifier.size(16.dp))

        Spinner(onOptionSelected = onOptionSelected)

    }
}


@Composable
fun Spinner(onOptionSelected: (String) -> Unit) {
    val argList = listOf("실버", "골드", "프리즘") // 증강 리스트
    var expanded by remember { mutableStateOf(false) }
    var selectedSecondArgs by remember { mutableStateOf("선택 하기") }

    Column(
        modifier = Modifier.wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(120.dp)
        ) {
            Row {
                Text(selectedSecondArgs, color = TftHelperColor.Grey)
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TftHelperColor.Grey
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(120.dp)
                .background(TftHelperColor.White) // 전체 배경색 설정
        ) {
            argList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedSecondArgs = option
                        expanded = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
fun ShowSecondAugProbabilities(probabilitis: List<Pair<String, Int>>) {
    Column {
        Text(
            text = "두번째 증강 확률",
            style = TextStyle(color = TftHelperColor.White),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        probabilitis.forEach { (augment, probability) ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = augment,
                    style = TextStyle(color = TftHelperColor.White, fontSize = 16.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                Text(
                    text = "$probability %",
                    style = TextStyle(color = TftHelperColor.White, fontSize = 16.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }

        }
    }
}

@Composable
fun ShowSecondThirdAugProbabilities(probabilities: List<Triple<String, String, Int>>) {
    Column {
        Text(
            text = "두번째, 세번째 증강 확률",
            style = TextStyle(color = TftHelperColor.White,fontSize = 14.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        probabilities.forEach { (second, third, probability) ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "$second  |  $third",
                    style= TextStyle(color=TftHelperColor.White, fontSize = 16.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                Text(
                    text = "$probability %",
                    style = TextStyle(color = TftHelperColor.White, fontSize = 16.sp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Preview(name = "Normal Device", widthDp = 360, heightDp = 640)
@Preview(name = "Large Device", widthDp = 600, heightDp = 960)
@Preview(
    name = "Galaxy Flip Preview",
    device = "spec:shape=Normal,width=1080,height=2636,unit=px,dpi=420"
)
@Composable
fun SecondScreenPreview() {
    TFThelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black)
        ) {
            val navController = rememberNavController()
            val selectedOption = "프리즘"
            SecondPage(navController = navController, firstAug = selectedOption)
        }
    }
}
