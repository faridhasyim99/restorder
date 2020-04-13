package com.bytotech.Restorder.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.CommonClass.Validate;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseProfile;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
	public PreferenceUtils preferenceUtils;
	private CommonClass cc;
	private TextView edtName, edtEmail, edtContactNo, edtAddress1, edtAddress2,
	edtCountry, edtState, edtCity, edtCode;
	private String strName, strEmail, strPassword, strContactNo;
	private ImageView ivBack, ivRegister;
	private CircleImageView ivProfileImage;
	private RelativeLayout progressbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		cc = new CommonClass(this);
		preferenceUtils = new PreferenceUtils(this);
		
		progressbar = findViewById(R.id.progressbar);
		edtName = findViewById(R.id.edtName);
		edtEmail = findViewById(R.id.edtEmail);
		edtContactNo = findViewById(R.id.edtContactNo);
		edtAddress1 = findViewById(R.id.edtAddress1);
		edtAddress2 = findViewById(R.id.edtAddress2);
		edtCountry = findViewById(R.id.edtCountry);
		edtState = findViewById(R.id.edtState);
		edtCity = findViewById(R.id.edtCity);
		edtCode = findViewById(R.id.edtCode);
		ivProfileImage = findViewById(R.id.ivProfileImage);
		ivBack = findViewById(R.id.ivBack);
		ivRegister = findViewById(R.id.ivRegister);
		
		edtName.setClickable(false);
		edtEmail.setClickable(false);
		edtContactNo.setClickable(false);
		
		findViewById(R.id.flCart).setVisibility(View.GONE);
		
		if (cc.isOnline()) {
			getProfile();
		} else {
			cc.showToast(getString(R.string.msg_no_internet));
		}
		
		ivBack.setOnClickListener(this);
		ivRegister.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.ivBack:
				onBackPressed();
				break;
			case R.id.ivRegister:
				Intent intent = new Intent(this, EditProfileActivity.class);
				startActivity(intent);
				break;
		}
	}
	
	public void getProfile() {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseProfile> call = dawaAPI.getProfile(preferenceUtils.getUserId());
		call.enqueue(new Callback<ResponseProfile>() {
			@SuppressLint("CheckResult")
			@Override
			public void onResponse(Call<ResponseProfile> call, Response<ResponseProfile> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						edtName.setText(response.body().get_user_profile.name);
						edtEmail.setText(response.body().get_user_profile.email);
						edtContactNo.setText(response.body().get_user_profile.phone);
						edtAddress1.setText(response.body().get_user_profile.address_line_1);
						edtAddress2.setText(response.body().get_user_profile.address_line_2);
						edtCity.setText(response.body().get_user_profile.city);
						edtState.setText(response.body().get_user_profile.state);
						edtCountry.setText(response.body().get_user_profile.country);
						edtCode.setText(response.body().get_user_profile.zipcode);
						preferenceUtils.setUserImage(response.body().get_user_profile.user_image);
						RequestOptions requestOptions = new RequestOptions();
						requestOptions.placeholder(R.drawable.user);
						requestOptions.error(R.drawable.user);
						
						StringBuilder sb = new StringBuilder();
						if (Validate.isNotNull(response.body().get_user_profile.address_line_1)) {
							sb.append(response.body().get_user_profile.address_line_1 + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.address_line_2)) {
							sb.append(response.body().get_user_profile.address_line_2 + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.city)) {
							sb.append(response.body().get_user_profile.city + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.state)) {
							sb.append(response.body().get_user_profile.state + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.country)) {
							sb.append(response.body().get_user_profile.country + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.zipcode)) {
							sb.append(response.body().get_user_profile.zipcode + " , ");
						}
						preferenceUtils.setAddress(sb.toString());
						preferenceUtils.setPhoneno(response.body().get_user_profile.phone);
						Log.e("Address==>", "" + sb.toString());
						
						Glide.with(ProfileActivity.this)
							   .setDefaultRequestOptions(requestOptions)
							   .load(response.body().get_user_profile.user_image)
							   .into(ivProfileImage);
						
					} else {
						cc.showToast(response.body().message);
					}
				} else {
					progressbar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseProfile> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	@Override
	protected void onResume() {
		if (cc.isOnline()) {
			getProfile();
		} else {
			cc.showToast(getString(R.string.msg_no_internet));
		}
		super.onResume();
	}
}
