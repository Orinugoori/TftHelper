# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# This is generated automatically by the Android Gradle plugin.

-dontwarn android.media.LoudnessCodecController$OnLoudnessCodecUpdateListener
-dontwarn android.media.LoudnessCodecController


# 기본 Android ProGuard 설정
-keep public class * extends android.app.Application
-keep public class * extends androidx.lifecycle.ViewModel

# Gson 및 데이터 모델 보호
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.orinugoori.tfthelper.AugmentResponse { *; }
-keep class com.orinugoori.tfthelper.Augment { *; }
-keep class com.orinugoori.tfthelper.ImageInfo { *; }
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp 및 네트워크 라이브러리 보호
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Compose 및 코루틴 관련 규칙 (사용 중이라면 유지)
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material.** { *; }
-dontwarn androidx.compose.**

# 디버깅용 스택 트레이스 유지
-keepattributes SourceFile,LineNumberTable

# 필요에 따라 소스 파일 이름 숨기기
-renamesourcefileattribute SourceFile


