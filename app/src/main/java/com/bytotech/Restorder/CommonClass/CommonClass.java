package com.bytotech.Restorder.CommonClass;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.bytotech.Restorder.Activity.BasketActivity;
import com.bytotech.Restorder.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class CommonClass {
	
	private static final String TAG = CommonClass.class.getSimpleName();
	private Context context;
	private Activity activity;
	private PreferenceUtils preferenceUtils;
	
	public CommonClass(Context context) {
		this.context = context;
		this.activity = (Activity) context;
		preferenceUtils = new PreferenceUtils(context);
	}
	
	public CommonClass(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}
	
	public boolean isOnline() {
		return Utility.isOnline(context);
	}
	
	public void showAlert(@NonNull String message) {
		MessageUtils.showAlert(activity, message);
	}
	
	// TOAST
	public void showToast(@NonNull String message) {
		MessageUtils.showToast(context, message);
	}
	
	public void showToast(int resId) {
		MessageUtils.showToast(context, resId);
	}
	
	public void linkShare(String title, String url) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		share.putExtra(Intent.EXTRA_SUBJECT, title);
		share.putExtra(Intent.EXTRA_TEXT, url);
		
		context.startActivity(Intent.createChooser(share, title));
	}
	
	public void AnimationRight(final RecyclerView recyclerView) {
		final Context context = recyclerView.getContext();
		final LayoutAnimationController controller =
			   AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);
		
		recyclerView.setLayoutAnimation(controller);
		recyclerView.getAdapter().notifyDataSetChanged();
		recyclerView.scheduleLayoutAnimation();
	}
	
	public void AnimationLeft(final RecyclerView recyclerView) {
		final Context context = recyclerView.getContext();
		final LayoutAnimationController controller =
			   AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_left);
		
		recyclerView.setLayoutAnimation(controller);
		recyclerView.getAdapter().notifyDataSetChanged();
		recyclerView.scheduleLayoutAnimation();
	}
	
	public void logout() {
		preferenceUtils.setUserId("");
		preferenceUtils.setUserName("");
		preferenceUtils.setUserEmail("");
		preferenceUtils.setUserImage("");
		preferenceUtils.setAddress("");
		preferenceUtils.setPhoneno("");
		preferenceUtils.setIsFbLogin(false);
	}
	
	public void startCartActivity() {
		Intent intent = new Intent(context, BasketActivity.class);
		ActivityOptions options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in, R.anim.slide_out);
		context.startActivity(intent, options.toBundle());
	}
	
	public void showFullAd() {
		final InterstitialAd mInterstitialAd = new InterstitialAd(context);
		mInterstitialAd.setAdUnitId(context.getString(R.string.interstitial_id));
		AdRequest adRequest = new AdRequest.Builder().build();
		mInterstitialAd.loadAd(adRequest);
		
		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
					mInterstitialAd.show();
				}
			}
			
			public void onAdFailedToLoad(com.google.ads.AdRequest.ErrorCode errorCode) {
			}
			
			@Override
			public void onAdOpened() {
			}
			
			@Override
			public void onAdLeftApplication() {
			}
			
			@Override
			public void onAdClosed() {
			}
		});
	}
}