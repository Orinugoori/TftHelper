package com.orinugoori.tfthelper.screen

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.orinugoori.tfthelper.AdViewModel
import com.orinugoori.tfthelper.CaculateProbabilities
import com.orinugoori.tfthelper.NextBtn
import com.orinugoori.tfthelper.ShowSelectedArg
import com.orinugoori.tfthelper.Title
import com.orinugoori.tfthelper.ui.theme.TFThelperTheme
import com.orinugoori.tfthelper.ui.theme.TftHelperColor


@Composable
fun ThirdPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    selectedOptions: String,
    adViewModel: AdViewModel
) {
    val options = selectedOptions.split(",")
    val firstOption = options[0]
    val secondOption = options[1]

    val context = LocalContext.current
    val activity = context as? Activity

    var showError by remember { mutableStateOf(false) }

    if (activity == null) {
        Log.d("광고 테스트", "액티비티 없어용")
    } else {
        Log.d("광고 테스트", "액티비티 있어용")
    }


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

        val thirdProbabilities =
            calculateLogic.calculateThirdProbabilities(firstOption, secondOption)
        ShowThirdAugmentProbabilities(probabilities = thirdProbabilities)

        NextBtn(
            text = "처음으로 돌아가기",
            errorText = "",
            showError= showError,
            onClick = {
                if (adViewModel.interstitialAd != null && activity != null) {
                    Log.d("광고 테스트", "광고를 표시합니다.")
                    adViewModel.interstitialAd?.show(activity)

                } else {
                    Log.d("광고 테스트", "광고가 안나와용")
                    navController.navigate("firstPage") {
                        popUpTo("firstPage") { inclusive = true }
                    }
                }
            }
        )

        adViewModel.loadInterstitialAd(activity!!) {
            navController.navigate("firstPage") {
                popUpTo("firstPage") { inclusive = true }
            }
        }


    }
}

@Composable
fun ShowThirdAugmentProbabilities(probabilities: List<Pair<String, Int>>) {
    Column {
        Text(
            text = "세번째 증강 확률",
            style = TextStyle(color = TftHelperColor.White),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        probabilities.forEach { (third, probability) ->
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
@Preview(name = "Normal Device", widthDp = 360, heightDp = 640)
@Preview(name = "Large Device", widthDp = 600, heightDp = 960)
@Preview(
    name = "Galaxy Flip Preview",
    device = "spec:shape=Normal,width=1080,height=2636,unit=px,dpi=420"
)
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
            ThirdPage(
                navController = navController,
                selectedOptions = selectedOptions,
                adViewModel = AdViewModel()
            )
        }
    }
}
