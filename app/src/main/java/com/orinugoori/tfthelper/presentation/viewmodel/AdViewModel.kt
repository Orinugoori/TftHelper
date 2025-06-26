package com.orinugoori.tfthelper.presentation.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.orinugoori.tfthelper.constants.AppConstants

class AdViewModel : ViewModel() {
    var interstitialAd: InterstitialAd? = null

    fun loadInterstitialAd(activity: Activity, onInterstitialAdClosed: () -> Unit) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            AppConstants.ADMOB_INTERSTITIAL_AD_UNIT_ID, // 테스트 전면 광고 ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("광고 테스트", "광고가 잘 나오고 있습니다.")
                    interstitialAd = ad

                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d("광고 테스트", "광고가 닫혔습니다.")
                            interstitialAd = null
                            onInterstitialAdClosed() // 광고 닫힌 후 동작 실행
                            loadInterstitialAd(activity, onInterstitialAdClosed) // 새 광고 로드
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("광고 테스트", "광고 로드 실패: ${adError.message}")
                    interstitialAd = null
                }
            }
        )
    }
}
