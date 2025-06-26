package com.orinugoori.tfthelper.presentation.screens.splash

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
import com.orinugoori.tfthelper.presentation.theme.TFTHelperTheme
import com.orinugoori.tfthelper.presentation.theme.TftHelperColor
import com.orinugoori.tfthelper.core.constants.AppConstants
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

        AsyncImage(
            model = "android.resource://com.orinugoori.tfthelper/drawable/tft_helper_text_logo",
            contentDescription = AppConstants.APP_NAME,
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = AppConstants.APP_DESCRIPTION,
            color = TftHelperColor.White,
            fontSize = 12.sp
        )

        LaunchedEffect(Unit) {
            delay(AppConstants.UI.SPLASH_DURATION)
            onSplashFinished()
        }
    }
}

@Composable
fun GifLoader() {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data("android.resource://com.orinugoori.tfthelper/drawable/tft_helper_logo")
                .apply { crossfade(true) }
                .build(),
            imageLoader = imageLoader
        ),
        contentDescription = AppConstants.APP_NAME,
        modifier = Modifier.size(200.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TFTHelperTheme {
        SplashScreen(onSplashFinished = {})
    }
}
