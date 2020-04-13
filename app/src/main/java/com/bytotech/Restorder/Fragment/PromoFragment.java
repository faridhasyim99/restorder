package com.bytotech.Restorder.Fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.Adapter.PromoAdapter;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseOfferList;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class PromoFragment extends Fragment {
	
	private View view;
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private RelativeLayout progressbar;
	private RecyclerView rvPromo;
	private TextView tvNoData;
	private List<ResponseOfferList.promoCode> promoCodes;
	private PromoAdapter promoAdapter;
	private Dialog dialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_promo, container, false);
		cc = new CommonClass(getActivity());
		preferenceUtils = new PreferenceUtils(getActivity());
		
		progressbar = view.findViewById(R.id.progressbar);
		tvNoData = view.findViewById(R.id.tvNoData);
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
		rvPromo = view.findViewById(R.id.rvPromo);
		rvPromo.setNestedScrollingEnabled(false);
		rvPromo.setHasFixedSize(false);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		rvPromo.setLayoutManager(layoutManager);
		
		if (cc.isOnline()) {
			getOfferList();
		} else {
			cc.showAlert(getString(R.string.msg_no_internet));
		}
		
		return view;
	}
	
	public void getOfferList() {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseOfferList> call = dawaAPI.getOfferList();
		call.enqueue(new Callback<ResponseOfferList>() {
			@Override
			public void onResponse(Call<ResponseOfferList> call, Response<ResponseOfferList> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						promoCodes = new ArrayList<>();
						promoCodes = response.body().promo_code;
						promoAdapter = new PromoAdapter(getActivity(), promoCodes);
						rvPromo.setAdapter(promoAdapter);
						cc.AnimationLeft(rvPromo);
						
						promoAdapter.setTermClick(new PromoAdapter.OnTermClick() {
							@Override
							public void OnTermClick(int position) {
								TermDialog(promoCodes.get(position).promo_policy);
							}
						});
						
						if (promoCodes.size() == 0) {
							tvNoData.setVisibility(View.VISIBLE);
							rvPromo.setVisibility(View.GONE);
						} else {
							tvNoData.setVisibility(View.GONE);
							rvPromo.setVisibility(View.VISIBLE);
						}
					} else {
						cc.showToast(response.body().message);
					}
				} else {
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseOfferList> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void TermDialog(String strPolicy) {
		dialog = new Dialog(getActivity());
		Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.custom_dialog_term);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);
		
		TextView tvPromoTerms = dialog.findViewById(R.id.tvPromoTerms);
		Button btnOk = dialog.findViewById(R.id.btnOk);
		tvPromoTerms.setText(Html.fromHtml(strPolicy));
		
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
}
