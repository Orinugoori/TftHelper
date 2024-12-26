package com.example.tfthelper.splashScreen


import android.content.Context
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.tfthelper.ui.theme.TFThelperTheme
import com.example.tfthelper.ui.theme.TftHelperColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = TftHelperColor.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GifLoader()

        AsyncImage(model = "android.resource://com.example.tfthelper/drawable/tft_helper_text_logo", contentDescription = null, modifier = Modifier.size(200.dp) )

        Text("롤토체스 증강확률 계산 | 증강 리스트 확인 ", color = TftHelperColor.White, fontSize = 12.sp)

        LaunchedEffect(Unit) {
            delay(3000)
            onSplashFinished()
        }
    }
}


@Composable
fun GifLoader() {
    val context = LocalContext.current
    val imageLoader = createGifImageLoader(context) // GIF 지원 ImageLoader 생성

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data("android.resource://com.example.tfthelper/raw/rotate_spatula") // GIF 경로
            .build(),
        imageLoader = imageLoader // 커스텀 ImageLoader 사용
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.size(250.dp)
    )
}

fun createGifImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory()) // API 28 이상
            } else {
                add(GifDecoder.Factory()) // API 27 이하
            }
        }
        .build()
}


@Preview(showBackground = true)
@Preview(name = "Normal Device", widthDp = 360, heightDp = 640)
@Preview(name = "Large Device", widthDp = 600, heightDp = 960)
@Preview(
    name = "Galaxy Flip Preview",
    device = "spec:shape=Normal,width=1080,height=2636,unit=px,dpi=420"
)
@Composable
fun SplashScreenPreview() {
    TFThelperTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TftHelperColor.Black)
        ) {
            val navController = rememberNavController()
            val selectedOptions = "프리즘,프리즘"
            SplashScreen({})
        }
    }
}