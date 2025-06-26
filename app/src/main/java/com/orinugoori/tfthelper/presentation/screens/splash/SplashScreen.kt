package com.orinugoori.tfthelper.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.orinugoori.tfthelper.presentation.theme.TftHelperColor
import com.orinugoori.tfthelper.core.constants.AppConstants
import kotlinx.coroutines.delay

/**
 * 스플래시 화면 컴포저블
 * Clean Architecture의 Presentation Layer
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = TftHelperColor.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // GIF 로딩 컴포넌트
        GifLoader()
        
        // 앱 로고
        AsyncImage(
            model = "android.resource://com.orinugoori.tfthelper/drawable/tft_helper_text_logo",
            contentDescription = "TFT Helper 로고",
            modifier = Modifier.size(200.dp)
        )
        
        // 앱 설명
        Text(
            text = AppConstants.APP_DESCRIPTION,
            color = TftHelperColor.White,
            fontSize = 12.sp
        )
        
        // 스플래시 타이머
        LaunchedEffect(Unit) {
            delay(AppConstants.SPLASH_DELAY)
            onSplashFinished()
        }
    }
}

/**
 * GIF 로더 컴포넌트
 * 회전하는 뒤집개 GIF를 표시
 */
@Composable
private fun GifLoader() {
    val context = LocalContext.current
    val imageLoader = createGifImageLoader(context)
    
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data("android.resource://com.orinugoori.tfthelper/raw/rotate_spatula")
            .build(),
        imageLoader = imageLoader
    )
    
    Image(
        painter = painter,
        contentDescription = "로딩 애니메이션",
        modifier = Modifier.size(100.dp)
    )
}

/**
 * GIF 지원을 위한 ImageLoader 생성 함수
 */
private fun createGifImageLoader(context: android.content.Context): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
}