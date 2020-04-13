package com.bytotech.Restorder.Fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.Adapter.OrderAdapter;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseOrder;
import com.bytotech.Restorder.WS.Response.ResponseRating;
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
public class OrderFragment extends Fragment {
	private View view;
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private RelativeLayout progressbar;
	private RecyclerView rvOrder;
	private TextView tvNoData;
	private List<ResponseOrder.orderSummary> orderSummaries;
	private OrderAdapter orderAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_order, container, false);
		cc = new CommonClass(getActivity());
		preferenceUtils = new PreferenceUtils(getActivity());
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
		tvNoData = view.findViewById(R.id.tvNoData);
		progressbar = view.findViewById(R.id.progressbar);
		rvOrder = view.findViewById(R.id.rvOrder);
		rvOrder.setNestedScrollingEnabled(false);
		rvOrder.setHasFixedSize(false);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		rvOrder.setLayoutManager(layoutManager);
		
		if (cc.isOnline()) {
			getOrderList();
		} else {
			cc.showAlert(getString(R.string.msg_no_internet));
		}
		
		return view;
	}
	
	public void getOrderList() {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseOrder> call = dawaAPI.getOrderList(preferenceUtils.getUserId());
		call.enqueue(new Callback<ResponseOrder>() {
			@Override
			public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						orderSummaries = new ArrayList<>();
						orderSummaries = response.body().user_order_list;
						orderAdapter = new OrderAdapter(getActivity(), orderSummaries);
						rvOrder.setAdapter(orderAdapter);
						
						if (orderSummaries.size() == 0) {
							tvNoData.setVisibility(View.VISIBLE);
							rvOrder.setVisibility(View.GONE);
						} else {
							tvNoData.setVisibility(View.GONE);
							rvOrder.setVisibility(View.VISIBLE);
						}
						
						/*orderAdapter.setOnRateUsClick(new OrderAdapter.OnRateUsClick() {
							@Override
							public void OnRateUsClick(int position) {
								commentDialog(position);
							}
						});*/
					} else {
						tvNoData.setVisibility(View.VISIBLE);
						rvOrder.setVisibility(View.GONE);
						cc.showToast(response.body().message);
					}
				} else {
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseOrder> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void commentDialog(final int position) {
		final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
		Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.custom_rate_us_dialog);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);
		
		final EditText etCommentText = dialog.findViewById(R.id.etCommentText);
		Button btnCancel = dialog.findViewById(R.id.btnCancel);
		Button btnComment = dialog.findViewById(R.id.btnComment);
		final RatingBar rating = dialog.findViewById(R.id.rating);
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		btnComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strRating = String.valueOf(rating.getRating());
				String strMsg = etCommentText.getText().toString().trim();
				if (cc.isOnline()) {
//					addRating(strRating, position,strMsg);
					dialog.dismiss();
				} else {
					cc.showToast(getString(R.string.msg_no_internet));
					
				}
			}
		});
		dialog.show();
	}
	
	/*public void addRating(String rating, int i,String strMsg) {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseRating> call = dawaAPI.getRating(preferenceUtils.getUserId(), rating,strMsg ,orderSummaries.get(i).menu_id);
		call.enqueue(new Callback<ResponseRating>() {
			@Override
			public void onResponse(Call<ResponseRating> call, Response<ResponseRating> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						cc.showAlert(response.body().response.message);
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseRating> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}*/
}
