package com.example.tfthelper.splashScreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tfthelper.HideStatusBarScreen
import com.example.tfthelper.MainActivity

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            HideStatusBarScreen()

            SplashScreen {
                startActivity(Intent(this, MainActivity::class.java))
                finish() // 스플래시 종료 후 현재 액티비티 제거
            }
        }
    }
}
