package com.bytotech.Restorder.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.Adapter.ViewPagerAdapter;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseSlider;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class WelcomeActivity extends AppCompatActivity {
	ViewPagerAdapter viewPagerAdapter;
	Timer timer;
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private ViewPager viewPager;
	private Button btnSkip;
	private List<ResponseSlider.SliderList> sliderList;
	private TextView tvImageTitle, tvImageDesc;
	private RelativeLayout progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferenceUtils = new PreferenceUtils(this);
		if (!preferenceUtils.isFirstTimeLaunch()) {
			launchHomeScreen();
			finish();
		}
		
		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
		
		setContentView(R.layout.activity_welcome);
		
		cc = new CommonClass(this);
		progressBar = findViewById(R.id.progressbar);
		viewPager = findViewById(R.id.view_pager);
		btnSkip = findViewById(R.id.btn_skip);
		tvImageTitle = findViewById(R.id.tvImageTitle);
		tvImageDesc = findViewById(R.id.tvImageDesc);
		Animation blink = AnimationUtils.loadAnimation(this, R.anim.blink);
		
		btnSkip.setAnimation(blink);
		if (cc.isOnline()) {
			getLogin();
		} else {
			cc.showToast(getString(R.string.msg_no_internet));
		}
		
		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchHomeScreen();
			}
		});
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
			}
			
			@Override
			public void onPageSelected(int i) {
				tvImageTitle.setText(sliderList.get(i).banner_name);
				tvImageDesc.setText(sliderList.get(i).banner_desc);
			}
			
			@Override
			public void onPageScrollStateChanged(int i) {
			
			}
		});
	}
	
	private int getItem(int i) {
		return viewPager.getCurrentItem() + i;
	}
	
	public void getLogin() {
		progressBar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseSlider> call = dawaAPI.getSlider();
		call.enqueue(new Callback<ResponseSlider>() {
			@Override
			public void onResponse(Call<ResponseSlider> call, Response<ResponseSlider> response) {
				if (response.isSuccessful()) {
					progressBar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						sliderList = new ArrayList<>();
						sliderList = response.body().slider_list;
						viewPagerAdapter = new ViewPagerAdapter(WelcomeActivity.this, sliderList);
						viewPager.setAdapter(viewPagerAdapter);
						PageIndicatorView indicator = findViewById(R.id.indicator);
						indicator.setViewPager(viewPager);
						tvImageTitle.setText(sliderList.get(0).banner_name);
						tvImageDesc.setText(sliderList.get(0).banner_desc);
						TimerTask timerTask = new TimerTask() {
							@Override
							public void run() {
								viewPager.post(new Runnable() {
									
									@Override
									public void run() {
										if ((viewPager.getCurrentItem() + 1) == (sliderList.size())) {
											viewPager.setCurrentItem(0);
										} else {
											viewPager.setCurrentItem((viewPager.getCurrentItem() + 1));
										}
									}
								});
							}
						};
						timer = new Timer();
						timer.schedule(timerTask, 3000, 3000);
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					progressBar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseSlider> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	private void launchHomeScreen() {
		if (preferenceUtils.isFirstTimeLaunch()) {
			startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
		} else {
			startActivity(new Intent(WelcomeActivity.this, SplashScreenActivity.class));
		}
		preferenceUtils.setFirstTimeLaunch(false);
		finish();
	}
}
