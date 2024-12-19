package com.example.tfthelper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import coil.compose.rememberImagePainter
import com.example.tfthelper.ui.theme.TFThelperTheme
import com.example.tfthelper.ui.theme.TftHelperColor
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.exp


@Composable
fun AugmentPage(
    viewModel: AugmentViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    var selectedTier by remember { mutableStateOf("전체") }

    Column(
        modifier
            .padding(top = 64.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        val keywordList = viewModel.keywordList.collectAsState().value

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "증강 리스트",
                style = TextStyle(
                    TftHelperColor.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            FilterSpinner(
                optionList = keywordList,
                onOptionSelected = { option ->
                    viewModel.filterAugmentsByKeyword(option)
                }
            )

        }


        val augList = listOf("전체", "실버", "골드", "프리즘")
        AugmentSelectButton(
            options = augList,
            defaultSelection = selectedTier,
            onOptionSelected = { selected ->
                selectedTier = selected
                viewModel.filterAugmentsByTier(selected)
            })

        Spacer(modifier = Modifier.size(8.dp))

        ShowAugments(viewModel)
    }
}


@Composable
fun FilterSpinner(optionList: Set<String>, onOptionSelected: (String) -> Unit) {

    CustomDropdownMenu(options = optionList, onOptionSelected = onOptionSelected)

}


@Composable
fun AugmentSelectButton(
    options: List<String>,
    defaultSelection: String?,
    onOptionSelected: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf(defaultSelection) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
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

                    else -> Brush.linearGradient(listOf(TftHelperColor.Grey, TftHelperColor.Grey))
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
                        onOptionSelected(selectedOption ?: "")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (selectedOption == option) Color.White else Color.White,
                    style = if (selectedOption == option) TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    else TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }

}

@Composable
fun ShowAugments(viewModel: AugmentViewModel = viewModel()) {

    val augments by viewModel.filteredAugments.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(augments) { augment ->
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = rememberImagePainter("https://ddragon.leagueoflegends.com/cdn/14.24.1/img/tft-augment/${augment.image.full}"),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = augment.name, style = TextStyle(TftHelperColor.White))
                    Text(
                        text = augment.description,
                        style = TextStyle(TftHelperColor.White, fontSize = 12.sp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AugmentScreenPreview() {
    TFThelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black)
        ) {
            val navController = rememberNavController()
            AugmentPage(navController = navController, viewModel = AugmentViewModel())
        }
    }
}
