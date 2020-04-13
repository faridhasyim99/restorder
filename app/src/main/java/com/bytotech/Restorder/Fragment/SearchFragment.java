package com.bytotech.Restorder.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.Adapter.SearchAdapter;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.CommonClass.Validate;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseSearch;
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
public class SearchFragment extends Fragment {
	private View view;
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private RelativeLayout progressbar;
	private EditText edtSearch;
	private TextView tvNoData;
	private ImageView ivSearch;
	private RecyclerView rvSearch;
	private List<ResponseSearch.menuSearch> menuSearches;
	private SearchAdapter searchAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_search, container, false);
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
		
		edtSearch = view.findViewById(R.id.edtSearch);
		ivSearch = view.findViewById(R.id.btnSearch);
		progressbar = view.findViewById(R.id.progressbar);
		tvNoData = view.findViewById(R.id.tvNoData);
		rvSearch = view.findViewById(R.id.rvSearch);
		rvSearch.setNestedScrollingEnabled(false);
		rvSearch.setHasFixedSize(false);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		rvSearch.setLayoutManager(layoutManager);
		
		ivSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strSearch = edtSearch.getText().toString().trim();
				if (Validate.isNotNull(strSearch)) {
					getSearch(strSearch);
				} else {
					cc.showToast("Please enter search keyword.");
				}
			}
		});
		
		return view;
	}
	
	public void getSearch(String strSearch) {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseSearch> call = dawaAPI.getSearch(strSearch);
		call.enqueue(new Callback<ResponseSearch>() {
			@Override
			public void onResponse(Call<ResponseSearch> call, Response<ResponseSearch> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						menuSearches = new ArrayList<>();
						menuSearches = response.body().menu_search;
						searchAdapter = new SearchAdapter(getActivity(), menuSearches);
						rvSearch.setAdapter(searchAdapter);
						
						cc.AnimationLeft(rvSearch);
						
						if (menuSearches.size() == 0) {
							tvNoData.setVisibility(View.VISIBLE);
							rvSearch.setVisibility(View.GONE);
						} else {
							tvNoData.setVisibility(View.GONE);
							rvSearch.setVisibility(View.VISIBLE);
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
			public void onFailure(Call<ResponseSearch> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
}
