package com.example.tfthelper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tfthelper.ui.theme.TftHelperColor

@Composable
fun Title(modifier: Modifier = Modifier) {
    Text(
        text = "TFT 증강체 확률 계산기",
        modifier = modifier,
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TftHelperColor.White)
    )
}


@Composable
fun NextBtn(modifier: Modifier = Modifier,text : String, onClick : () -> Unit ) {
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