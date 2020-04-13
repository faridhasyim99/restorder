package com.bytotech.Restorder.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.Adapter.MenuDetailAdapter;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseMenuDetail;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class MenuDetailListActivity extends AppCompatActivity {
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private RelativeLayout progressbar;
	private RecyclerView rvMenu;
	private TextView tvNoData;
	private List<ResponseMenuDetail.MenuListCat> menucategoryLists;
	private MenuDetailAdapter menuDetailAdapter;
	private String id;
	private ImageView ivBack, ivCart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_detail_list);
		
		cc = new CommonClass(this);
		preferenceUtils = new PreferenceUtils(this);
		id = getIntent().getStringExtra("id");
		if (preferenceUtils.getCartCount().equals("") || preferenceUtils.getCartCount().equals("0")) {
			findViewById(R.id.tvCartCount).setVisibility(View.GONE);
		} else {
			findViewById(R.id.tvCartCount).setVisibility(View.VISIBLE);
		}
		
		AdView adView = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		if (preferenceUtils.adCount() == 5) {
			preferenceUtils.setadCount(1);
			cc.showFullAd();
		} else {
			int count = preferenceUtils.adCount() + 1;
			preferenceUtils.setadCount(count);
		}
		
		((TextView) findViewById(R.id.tvCartCount)).setText(preferenceUtils.getCartCount());
		tvNoData = findViewById(R.id.tvNoData);
		ivBack = findViewById(R.id.ivBack);
		ivCart = findViewById(R.id.ivCart);
		progressbar = findViewById(R.id.progressbar);
		rvMenu = findViewById(R.id.rvMenu);
		rvMenu.setNestedScrollingEnabled(false);
		rvMenu.setHasFixedSize(false);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		rvMenu.setLayoutManager(layoutManager);
		
		if (cc.isOnline()) {
			getMenuDetailList();
		} else {
			cc.showToast(getString(R.string.msg_no_internet));
		}
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		ivCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cc.startCartActivity();
			}
		});
	}
	
	public void getMenuDetailList() {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseMenuDetail> call = dawaAPI.getMenuDetailList(id, preferenceUtils.getUserId());
		call.enqueue(new Callback<ResponseMenuDetail>() {
			@Override
			public void onResponse(Call<ResponseMenuDetail> call, Response<ResponseMenuDetail> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						menucategoryLists = new ArrayList<>();
						menucategoryLists = response.body().menu_list_cat;
						menuDetailAdapter = new MenuDetailAdapter(MenuDetailListActivity.this, menucategoryLists);
						rvMenu.setAdapter(menuDetailAdapter);
						cc.AnimationLeft(rvMenu);
						if (menucategoryLists.size() == 0) {
							tvNoData.setVisibility(View.VISIBLE);
							rvMenu.setVisibility(View.GONE);
						} else {
							tvNoData.setVisibility(View.GONE);
							rvMenu.setVisibility(View.VISIBLE);
						}
					} else {
						cc.showToast(response.body().message);
					}
				} else {
					progressbar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseMenuDetail> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		
	}
	
	@Override
	protected void onResume() {
		preferenceUtils = new PreferenceUtils(this);
		if (preferenceUtils.getCartCount().equals("") || preferenceUtils.getCartCount().equals("0")) {
			findViewById(R.id.tvCartCount).setVisibility(View.GONE);
		} else {
			findViewById(R.id.tvCartCount).setVisibility(View.VISIBLE);
		}
		((TextView) findViewById(R.id.tvCartCount)).setText(preferenceUtils.getCartCount());
		super.onResume();
	}
}
