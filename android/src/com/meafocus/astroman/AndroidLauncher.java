package com.meafocus.astroman;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class AndroidLauncher extends AndroidApplication  implements AdsController, RewardedVideoAdListener {

	private RewardedVideoAd rewardedVideoAd;
	Preferences preferencias;



	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new CoinMan(this), config);

		rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
		rewardedVideoAd.setRewardedVideoAdListener(this);

		loadRewardedVideoAd();
		preferencias = Gdx.app.getPreferences("coinAstro");

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
						View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


	}

	@Override
	public void showRewardedVideo() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(rewardedVideoAd.isLoaded()){
					rewardedVideoAd.show();
				}
				else loadRewardedVideoAd();
			}
		});

	}

	@Override
	public void loadRewardedVideoAd() {
		rewardedVideoAd.loadAd("ca-app-pub-6944068380993727/7558158687", // Test Ad
				new AdRequest.Builder().build());

	}

	@Override
	public void onRewardedVideoAdLoaded() {
//		Toast.makeText(this, "Ads Carregado", Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "Recompensa disponivel!", Toast.LENGTH_SHORT).show();
		preferencias.putInteger("podeContinuar", 1);
		preferencias.flush();


	}

	@Override
	public void onRewardedVideoAdOpened() {

	}

	@Override
	public void onRewardedVideoStarted() {
		Toast.makeText(this, "Assista até o final para ganhar a recompensa", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onRewardedVideoAdClosed() {

	}

	@Override
	public void onRewarded(RewardItem rewardItem) {
//		Toast.makeText(this, "Recompensa: "+rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "Você ganhou:\n- Manter pontos\n- Mais estrelas para capturar", Toast.LENGTH_LONG).show();

		preferencias.putInteger("recompensa", 1);
		preferencias.flush();
	}

	@Override
	public void onRewardedVideoAdLeftApplication() {



	}

	@Override
	public void onRewardedVideoAdFailedToLoad(int i) {

	}

	@Override
	public void onRewardedVideoCompleted() {

	}


}
