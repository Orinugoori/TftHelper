package com.example.tfthelper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.tfthelper.ui.theme.TFThelperTheme
import com.example.tfthelper.ui.theme.TftHelperColor



@Composable
fun ThirdPage(modifier: Modifier = Modifier, navController: NavHostController, selectedOptions : String) {
    val options = selectedOptions.split(",")
    val firstOption = options[0]
    val secondOption = options[1]

    Column(
        modifier
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        val calculateLogic = CaculateProbabilities()

        Title()

        Row(Modifier.fillMaxWidth()) {
            ShowSelectedArg(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                isFirst = true,
                prevSelectedArg = firstOption
            )


            ShowSelectedArg(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                isFirst = false,
                prevSelectedArg = secondOption
            )

        }

        val thirdProbabilities = calculateLogic.calculateThirdProbabilities(firstOption,secondOption)
        ShowThirdAugmentProbabilities(probabilities = thirdProbabilities)

        NextBtn(text = "처음으로 돌아가기", onClick = {navController.navigate("firstPage")})



    }
}

@Composable
fun ShowThirdAugmentProbabilities(probabilities: List<Pair<String, Int>> ) {
    Column {
        Text(
            text = "세번째 증강 확률",
            style = TextStyle(color = TftHelperColor.White),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        probabilities.forEach { (third, probability)->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = third,
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




@Preview(showBackground = true)
@Composable
fun ThirdScreenPreview() {
    TFThelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black)
        ) {
            val navController = rememberNavController()
            val selectedOptions = "프리즘,프리즘"
            ThirdPage(navController = navController, selectedOptions = selectedOptions)
        }
    }
}
