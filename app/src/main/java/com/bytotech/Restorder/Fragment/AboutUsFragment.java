package com.bytotech.Restorder.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseAboutUs;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class AboutUsFragment extends Fragment {
	private View view;
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private RelativeLayout progressbar;
	private TextView tvAboutUs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_about_us, container, false);
		cc = new CommonClass(getActivity());
		preferenceUtils = new PreferenceUtils(getActivity());
		progressbar = view.findViewById(R.id.progressbar);
		tvAboutUs = view.findViewById(R.id.tvAboutUs);
		AdView adView = view.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		if (preferenceUtils.adCount() == 5) {
			preferenceUtils.setadCount(1);
			cc.showFullAd();
		} else {
			int count = preferenceUtils.adCount() + 1;
			preferenceUtils.setadCount(count);
		}
		if (cc.isOnline()) {
			getAboutUs();
		} else {
			cc.showToast(getString(R.string.msg_no_internet));
		}
		
		return view;
	}
	
	public void getAboutUs() {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseAboutUs> call = dawaAPI.getAboutUs();
		call.enqueue(new Callback<ResponseAboutUs>() {
			@Override
			public void onResponse(Call<ResponseAboutUs> call, Response<ResponseAboutUs> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						tvAboutUs.setText(Html.fromHtml(response.body().about_us_detail.about_desc));
					} else {
						
						cc.showToast(response.body().message);
					}
				} else {
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseAboutUs> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
}
