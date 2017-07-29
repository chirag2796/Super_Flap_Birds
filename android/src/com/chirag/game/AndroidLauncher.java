package com.chirag.game;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AndroidLauncher extends AndroidApplication implements AdHandler{
	private static final String TAG = "AndroidLauncher";
	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;
	protected AdView adView;

	private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-5906576380747833/7458513703";
	InterstitialAd interstitialAd;
	AdRequest.Builder interstitialRequest;

	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case SHOW_ADS:
					adView.setVisibility(View.VISIBLE);
					break;
				case HIDE_ADS:
					adView.setVisibility(View.GONE);
					break;
			}
		}
	};

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		////test
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				//interstitialAd.show();
			}

			@Override
			public void onAdClosed() {
			}
		});

		////test

		//RelativeLayout layout = new RelativeLayout(this);
		FrameLayout layout = new FrameLayout(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		View gameView = initializeForView(new FlappyDemo(this), config);

		layout.addView(gameView);;

		adView = new AdView(this);;

		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				int visibility = adView.getVisibility();
				adView.setVisibility(AdView.GONE);
				adView.setVisibility(visibility);
			}
		});

		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId("ca-app-pub-5906576380747833/8935246907");

		AdRequest.Builder builder = new AdRequest.Builder();
		//builder.addTestDevice("7A6947F9FDE16207614A7CCEA6306196");
		/*RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);*/
		FrameLayout.LayoutParams adParams =  new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
		);

		layout.addView(adView, adParams);
		adView.loadAd(builder.build());

		setContentView(layout);

		//initialize(new FlappyDemo(), config);
	}

	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}

	@Override
	  public void showOrLoadInterstital() {
		    try {
			      runOnUiThread(new Runnable() {
				        public void run() {
					          if (interstitialAd.isLoaded()) {
						            interstitialAd.show();
						          }
					          else {
								  	interstitialRequest = new AdRequest.Builder();
						//		  	interstitialRequest.addTestDevice("7A6947F9FDE16207614A7CCEA6306196");
								  	interstitialAd.loadAd(interstitialRequest.build());
						          }
					        }
				      });
			    } catch (Exception e) {
			    }
		  }

    @Override
    public void loadInterstitial(){
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    interstitialRequest = new AdRequest.Builder();
              //      interstitialRequest.addTestDevice("7A6947F9FDE16207614A7CCEA6306196");
                    interstitialAd.loadAd(interstitialRequest.build());
                }
            });
        }

        catch (Exception e) {

        }
    }

    @Override
    public void showInterstitial(){
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            });
        }

        catch (Exception e) {

        }
    }
}
